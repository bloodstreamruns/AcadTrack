package com.example.acadtrack_beta.data.repository

import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.model.Tarea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Singleton en memoria (mismo patrón que Room usará más adelante, Tema 6 - 16.1)
// Fuente única de datos compartida entre AsignaturaViewModel y TareaViewModel
object TareaRepository {

    private val _asignaturas = MutableStateFlow<List<Asignatura>>(emptyList())
    val asignaturas: StateFlow<List<Asignatura>> = _asignaturas.asStateFlow()

    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> = _tareas.asStateFlow()

    // ---- Asignaturas ----

    fun getAllAsignaturas(): List<Asignatura> = _asignaturas.value

    fun guardarAsignatura(asignatura: Asignatura) {
        _asignaturas.update { lista ->
            if (lista.any { it.id == asignatura.id }) {
                lista.map { if (it.id == asignatura.id) asignatura else it }
            } else {
                lista + asignatura
            }
        }
    }

    // Devuelve false si no se pudo eliminar (tiene tareas pendientes)
    fun eliminarAsignatura(id: String): Boolean {
        val tienePendientes = _tareas.value.any { it.asignaturaId == id && !it.completada }
        if (tienePendientes) return false

        _asignaturas.update { lista -> lista.filterNot { it.id == id } }
        return true
    }

    // ---- Tareas ----

    fun getAllTareas(): List<Tarea> = _tareas.value

    fun getTareasPorAsignatura(asignaturaId: String): List<Tarea> =
        _tareas.value.filter { it.asignaturaId == asignaturaId }

    fun guardarTarea(tarea: Tarea) {
        _tareas.update { lista ->
            if (lista.any { it.id == tarea.id }) {
                lista.map { if (it.id == tarea.id) tarea else it }
            } else {
                lista + tarea
            }
        }
    }

    fun eliminarTarea(id: String) {
        _tareas.update { lista -> lista.filterNot { it.id == id } }
    }
}