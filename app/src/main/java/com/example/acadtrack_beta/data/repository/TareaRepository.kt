package com.example.acadtrack_beta.data.repository

import android.content.Context
import com.example.acadtrack_beta.data.local.AppDatabase
import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.model.Tarea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Fuente única de datos para Asignaturas y Tareas, respaldada por Room
object TareaRepository {

    private lateinit var database: AppDatabase
    private val repositorioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        if (!::database.isInitialized) {
            database = AppDatabase.obtenerBaseDatos(context)
        }
    }

    val asignaturas: StateFlow<List<Asignatura>> by lazy {
        database.asignaturaDao().obtenerTodas()
            .stateIn(repositorioScope, SharingStarted.Eagerly, emptyList())
    }

    val tareas: StateFlow<List<Tarea>> by lazy {
        database.tareaDao().obtenerTodas()
            .stateIn(repositorioScope, SharingStarted.Eagerly, emptyList())
    }

    // ---- Asignaturas ----

    fun getAllAsignaturas(): List<Asignatura> = asignaturas.value

    fun guardarAsignatura(asignatura: Asignatura) {
        repositorioScope.launch {
            database.asignaturaDao().insertarOActualizar(asignatura)
        }
    }

    fun eliminarAsignatura(id: String): Boolean {
        val tienePendientes = tareas.value.any { it.asignaturaId == id && !it.completada }
        if (tienePendientes) return false

        repositorioScope.launch {
            database.asignaturaDao().eliminarPorId(id)
        }
        return true
    }

    // ---- Tareas ----

    fun getAllTareas(): List<Tarea> = tareas.value

    fun getTareasPorAsignatura(asignaturaId: String): List<Tarea> =
        tareas.value.filter { it.asignaturaId == asignaturaId }

    fun guardarTarea(tarea: Tarea) {
        repositorioScope.launch {
            database.tareaDao().insertarOActualizar(tarea)
        }
    }

    fun eliminarTarea(id: String) {
        repositorioScope.launch {
            database.tareaDao().eliminarPorId(id)
        }
    }
}