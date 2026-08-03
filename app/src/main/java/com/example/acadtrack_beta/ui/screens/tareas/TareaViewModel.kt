package com.example.acadtrack_beta.ui.screens.tareas

import androidx.lifecycle.ViewModel
import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.model.Prioridad
import com.example.acadtrack_beta.data.model.Tarea
import com.example.acadtrack_beta.data.model.TipoTarea
import com.example.acadtrack_beta.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import androidx.lifecycle.viewModelScope
import com.example.acadtrack_beta.ui.util.coincideConBusqueda
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TareaViewModel : ViewModel() {

    val tareas: StateFlow<List<Tarea>> = TareaRepository.tareas
    val asignaturas: StateFlow<List<Asignatura>> = TareaRepository.asignaturas
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    // "tareas" (sin filtrar) sigue disponible tal cual. Esta es la versión filtrada
// para la lista visible; también busca por el nombre de la asignatura relacionada.
    val tareasFiltradas: StateFlow<List<Tarea>> = combine(
        TareaRepository.tareas,
        TareaRepository.asignaturas,
        _textoBusqueda
    ) { tareas, asignaturas, texto ->
        tareas.filter { tarea ->
            val nombreAsignatura = asignaturas.find { it.id == tarea.asignaturaId }?.nombre ?: ""
            coincideConBusqueda(
                texto,
                listOf(
                    tarea.titulo,
                    tarea.descripcion,
                    tarea.notas,
                    nombreAsignatura,
                    tarea.tipo.name,
                    tarea.prioridad.name
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onTextoBusquedaChanged(texto: String) {
        _textoBusqueda.value = texto
    }

    private val _formState = MutableStateFlow(TareaFormState())
    val formState: StateFlow<TareaFormState> = _formState.asStateFlow()

    private var editandoId: String? = null

    fun onTituloChanged(valor: String) {
        _formState.update { it.copy(titulo = valor, tituloError = null) }
    }

    fun onDescripcionChanged(valor: String) {
        _formState.update { it.copy(descripcion = valor) }
    }

    fun onAsignaturaSeleccionada(asignatura: Asignatura) {
        _formState.update {
            it.copy(
                asignaturaId = asignatura.id,
                asignaturaNombre = asignatura.nombre,
                asignaturaError = null
            )
        }
    }

    fun onFechaSeleccionada(fecha: LocalDate) {
        _formState.update { it.copy(fechaEntrega = fecha, fechaError = null) }
    }

    fun onTipoSeleccionado(tipo: TipoTarea) {
        _formState.update { it.copy(tipo = tipo) }
    }

    fun onPrioridadSeleccionada(prioridad: Prioridad) {
        _formState.update { it.copy(prioridad = prioridad) }
    }

    fun onNotasChanged(valor: String) {
        _formState.update { it.copy(notas = valor) }
    }

    fun cargarParaEditar(tarea: Tarea) {
        editandoId = tarea.id
        val asignatura = TareaRepository.getAllAsignaturas().find { it.id == tarea.asignaturaId }
        _formState.value = TareaFormState(
            titulo = tarea.titulo,
            descripcion = tarea.descripcion,
            asignaturaId = tarea.asignaturaId,
            asignaturaNombre = asignatura?.nombre ?: "",
            fechaEntrega = tarea.fechaEntrega.toLocalDate(),
            tipo = tarea.tipo,
            prioridad = tarea.prioridad,
            notas = tarea.notas
        )
    }

    fun limpiarFormulario() {
        editandoId = null
        _formState.value = TareaFormState()
    }

    fun guardar() {
        if (!validarCampos()) return

        val estado = _formState.value
        val idActual = editandoId ?: UUID.randomUUID().toString()
        val tarea = Tarea(
            id = idActual,
            titulo = estado.titulo.trim(),
            descripcion = estado.descripcion.trim(),
            asignaturaId = estado.asignaturaId!!,
            fechaEntrega = estado.fechaEntrega!!.atTime(LocalTime.of(23, 59)),
            tipo = estado.tipo,
            prioridad = estado.prioridad,
            notas = estado.notas.trim()
        )

        TareaRepository.guardarTarea(tarea)
        _formState.update { it.copy(guardadoExitoso = true) }
    }

    fun marcarCompletada(tarea: Tarea, completada: Boolean) {
        TareaRepository.guardarTarea(tarea.copy(completada = completada))
    }

    fun eliminar(id: String) {
        TareaRepository.eliminarTarea(id)
    }

    fun consumeGuardadoExitoso() {
        _formState.update { it.copy(guardadoExitoso = false) }
    }

    private fun validarCampos(): Boolean {
        val estado = _formState.value
        var esValido = true

        val tituloError = if (estado.titulo.isBlank()) "El título es obligatorio" else null
        if (tituloError != null) esValido = false

        val asignaturaError = if (estado.asignaturaId == null) "Selecciona una asignatura" else null
        if (asignaturaError != null) esValido = false

        val fechaError = if (estado.fechaEntrega == null) "Selecciona una fecha de entrega" else null
        if (fechaError != null) esValido = false

        _formState.update {
            it.copy(tituloError = tituloError, asignaturaError = asignaturaError, fechaError = fechaError)
        }
        return esValido
    }
}