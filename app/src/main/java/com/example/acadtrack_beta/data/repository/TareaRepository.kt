package com.example.acadtrack_beta.data.repository

import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.model.Prioridad
import com.example.acadtrack_beta.data.model.Tarea
import com.example.acadtrack_beta.data.model.TipoTarea
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
object TareaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val repositorioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val asignaturas: StateFlow<List<Asignatura>> = AuthRepository.usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) flowOf(emptyList()) else observarAsignaturas(usuario.uid)
        }
        .stateIn(repositorioScope, SharingStarted.Eagerly, emptyList())

    val tareas: StateFlow<List<Tarea>> = AuthRepository.usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) flowOf(emptyList()) else observarTareas(usuario.uid)
        }
        .stateIn(repositorioScope, SharingStarted.Eagerly, emptyList())

    private fun observarAsignaturas(uid: String): Flow<List<Asignatura>> = callbackFlow {
        val listener = db.collection("asignaturas")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.mapNotNull { it.toObject(Asignatura::class.java) }
                    ?.sortedBy { it.nombre } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    private fun observarTareas(uid: String): Flow<List<Tarea>> = callbackFlow {
        val listener = db.collection("tareas")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.mapNotNull { documentoATarea(it) }
                    ?.sortedBy { it.fechaEntrega } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    fun getAllAsignaturas(): List<Asignatura> = asignaturas.value

    fun guardarAsignatura(asignatura: Asignatura) {
        val uid = AuthRepository.uid ?: return
        val conUsuario = asignatura.copy(userId = uid)
        db.collection("asignaturas").document(conUsuario.id)
            .set(conUsuario)
            .addOnFailureListener { it.printStackTrace() }
    }

    fun eliminarAsignatura(id: String): Boolean {
        val tienePendientes = tareas.value.any { it.asignaturaId == id && !it.completada }
        if (tienePendientes) return false

        db.collection("asignaturas").document(id)
            .delete()
            .addOnFailureListener { it.printStackTrace() }
        return true
    }

    fun getAllTareas(): List<Tarea> = tareas.value

    fun getTareasPorAsignatura(asignaturaId: String): List<Tarea> =
        tareas.value.filter { it.asignaturaId == asignaturaId }

    fun guardarTarea(tarea: Tarea) {
        val uid = AuthRepository.uid ?: return
        db.collection("tareas").document(tarea.id)
            .set(tareaAMapa(tarea.copy(userId = uid)))
            .addOnFailureListener { it.printStackTrace() }
    }

    fun eliminarTarea(id: String) {
        db.collection("tareas").document(id)
            .delete()
            .addOnFailureListener { it.printStackTrace() }
    }

    private fun tareaAMapa(tarea: Tarea): Map<String, Any?> = mapOf(
        "id" to tarea.id,
        "userId" to tarea.userId,
        "titulo" to tarea.titulo,
        "descripcion" to tarea.descripcion,
        "asignaturaId" to tarea.asignaturaId,
        "fechaEntrega" to Timestamp(tarea.fechaEntrega.toEpochSecond(ZoneOffset.UTC), 0),
        "tipo" to tarea.tipo.name,
        "prioridad" to tarea.prioridad.name,
        "completada" to tarea.completada,
        "notas" to tarea.notas
    )

    private fun documentoATarea(documento: DocumentSnapshot): Tarea? {
        val fecha = documento.getTimestamp("fechaEntrega") ?: return null
        return Tarea(
            id = documento.id,
            userId = documento.getString("userId") ?: "",
            titulo = documento.getString("titulo") ?: "",
            descripcion = documento.getString("descripcion") ?: "",
            asignaturaId = documento.getString("asignaturaId") ?: "",
            fechaEntrega = LocalDateTime.ofEpochSecond(fecha.seconds, 0, ZoneOffset.UTC),
            tipo = documento.getString("tipo")?.let { TipoTarea.valueOf(it) } ?: TipoTarea.TAREA,
            prioridad = documento.getString("prioridad")?.let { Prioridad.valueOf(it) } ?: Prioridad.MEDIA,
            completada = documento.getBoolean("completada") ?: false,
            notas = documento.getString("notas") ?: ""
        )
    }
}