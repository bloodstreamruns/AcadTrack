package com.example.acadtrack_beta.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acadtrack_beta.data.model.Tarea
import com.example.acadtrack_beta.data.repository.TareaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

data class TareaConAsignatura(
    val tarea: Tarea,
    val nombreAsignatura: String,
    val atrasada: Boolean
)

data class HomeUiState(
    val totalAsignaturas: Int = 0,
    val tareasPendientes: List<TareaConAsignatura> = emptyList(),
    val totalPendientes: Int = 0,
    val totalAtrasadas: Int = 0
)

class HomeViewModel : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        TareaRepository.tareas,
        TareaRepository.asignaturas
    ) { tareas, asignaturas ->
        val ahora = LocalDateTime.now()
        val pendientes = tareas
            .filter { !it.completada }
            .sortedBy { it.fechaEntrega }
            .map { tarea ->
                val nombre = asignaturas.find { it.id == tarea.asignaturaId }?.nombre
                    ?: "Sin asignatura"
                TareaConAsignatura(
                    tarea = tarea,
                    nombreAsignatura = nombre,
                    atrasada = tarea.fechaEntrega.isBefore(ahora)
                )
            }

        HomeUiState(
            totalAsignaturas = asignaturas.size,
            tareasPendientes = pendientes,
            totalPendientes = pendientes.size,
            totalAtrasadas = pendientes.count { it.atrasada }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun marcarCompletada(tarea: Tarea, completada: Boolean) {
        TareaRepository.guardarTarea(tarea.copy(completada = completada))
    }
}