package com.example.acadtrack_beta.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acadtrack_beta.data.model.Prioridad
import com.example.acadtrack_beta.data.model.Tarea
import com.example.acadtrack_beta.data.repository.TareaRepository
import com.example.acadtrack_beta.ui.util.coincideConBusqueda
import kotlinx.coroutines.flow.MutableStateFlow
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
    val totalAtrasadas: Int = 0,
    val conteoPorPrioridad: Map<Prioridad, Int> = emptyMap()
)

class HomeViewModel : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    val uiState: StateFlow<HomeUiState> = combine(
        TareaRepository.tareas,
        TareaRepository.asignaturas,
        _textoBusqueda
    ) { tareas, asignaturas, texto ->
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

        // Contadores y dona: siempre sobre el total real de pendientes, SIN filtrar por texto.
        val conteoPorPrioridad = pendientes
            .groupingBy { it.tarea.prioridad }
            .eachCount()

        // Solo la lista que se muestra en pantalla se filtra por el texto de búsqueda.
        val pendientesFiltradas = pendientes.filter { item ->
            coincideConBusqueda(
                texto,
                listOf(
                    item.tarea.titulo,
                    item.tarea.descripcion,
                    item.tarea.notas,
                    item.nombreAsignatura,
                    item.tarea.tipo.name,
                    item.tarea.prioridad.name
                )
            )
        }

        HomeUiState(
            totalAsignaturas = asignaturas.size,
            tareasPendientes = pendientesFiltradas,
            totalPendientes = pendientes.size,
            totalAtrasadas = pendientes.count { it.atrasada },
            conteoPorPrioridad = conteoPorPrioridad
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onTextoBusquedaChanged(texto: String) {
        _textoBusqueda.value = texto
    }

    fun marcarCompletada(tarea: Tarea, completada: Boolean) {
        TareaRepository.guardarTarea(tarea.copy(completada = completada))
    }
}
