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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.ZoneOffset

// Fuente única de datos para Asignaturas y Tareas, ahora respaldada por Firestore.
object TareaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccionAsignaturas = db.collection("asignaturas")
    private val coleccionTareas = db.collection("tareas")

    private val repositorioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Lecturas en tiempo real: addSnapshotListener es el equivalente en Firestore
    // al Flow que nos daba Room — cualquier cambio remoto llega solo, sin refrescar nada.
    val asignaturas: StateFlow<List<Asignatura>> = callbackFlow {
        val listener = coleccionAsignaturas.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val lista = snapshot?.documents
                ?.mapNotNull { it.toObject(Asignatura::class.java) }
                ?.sortedBy { it.nombre }
                ?: emptyList()
            trySend(lista)
        }
        awaitClose { listener.remove() }
    }.stateIn(repositorioScope, SharingStarted.Eagerly, emptyList())

    val tareas: StateFlow<List<Tarea>> = callbackFlow {
        val listener = coleccionTareas.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val lista = snapshot?.documents
                ?.mapNotNull { documentoATarea(it) }
                ?.sortedBy { it.fechaEntrega }
                ?: emptyList()
            trySend(lista)
        }
        awaitClose { listener.remove() }
    }.stateIn(repositorioScope, SharingStarted.Eagerly, emptyList())

    // ---- Asignaturas ---- (mismos nombres de función de siempre)

    fun getAllAsignaturas(): List<Asignatura> = asignaturas.value

    fun guardarAsignatura(asignatura: Asignatura) {
        coleccionAsignaturas.document(asignatura.id)
            .set(asignatura)
            .addOnFailureListener { it.printStackTrace() }
    }

    fun eliminarAsignatura(id: String): Boolean {
        val tienePendientes = tareas.value.any { it.asignaturaId == id && !it.completada }
        if (tienePendientes) return false

        coleccionAsignaturas.document(id)
            .delete()
            .addOnFailureListener { it.printStackTrace() }
        return true
    }

    // ---- Tareas ----

    fun getAllTareas(): List<Tarea> = tareas.value

    fun getTareasPorAsignatura(asignaturaId: String): List<Tarea> =
        tareas.value.filter { it.asignaturaId == asignaturaId }

    fun guardarTarea(tarea: Tarea) {
        coleccionTareas.document(tarea.id)
            .set(tareaAMapa(tarea))
            .addOnFailureListener { it.printStackTrace() }
    }

    fun eliminarTarea(id: String) {
        coleccionTareas.document(id)
            .delete()
            .addOnFailureListener { it.printStackTrace() }
    }

    // ---- Conversión manual de Tarea ----
    // Firestore no sabe mapear LocalDateTime ni enums automáticamente con toObject(),
    // así que aquí se convierte a mano (fechaEntrega <-> Timestamp, enums <-> texto).

    private fun tareaAMapa(tarea: Tarea): Map<String, Any?> = mapOf(
        "id" to tarea.id,
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