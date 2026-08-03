package com.example.acadtrack_beta.ui.screens.asignaturas

import androidx.lifecycle.ViewModel
import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.example.acadtrack_beta.ui.util.coincideConBusqueda

data class AsignaturaConProgreso(
    val asignatura: Asignatura,
    val completadas: Int,
    val total: Int
)


class AsignaturaViewModel : ViewModel() {

    // Ahora lee directo del repositorio compartido, no de una lista propia
    val asignaturas: StateFlow<List<Asignatura>> = TareaRepository.asignaturas

    val progreso: StateFlow<List<AsignaturaConProgreso>> = combine(
        TareaRepository.asignaturas,
        TareaRepository.tareas
    ) { asignaturas, tareas ->
        asignaturas.map { asignatura ->
            val tareasAsignatura = tareas.filter { it.asignaturaId == asignatura.id }
            AsignaturaConProgreso(
                asignatura = asignatura,
                completadas = tareasAsignatura.count { it.completada },
                total = tareasAsignatura.size
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    // "asignaturas" (sin filtrar) sigue existiendo tal cual para el resto de la pantalla.
// Esta es la versión filtrada, exclusiva para la lista de tarjetas visible.
    val asignaturasFiltradas: StateFlow<List<Asignatura>> = combine(
        TareaRepository.asignaturas,
        _textoBusqueda
    ) { asignaturas, texto ->
        asignaturas.filter { asignatura ->
            coincideConBusqueda(
                texto,
                listOf(asignatura.nombre, asignatura.codigo, asignatura.profesor, asignatura.semestre)
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

    private val _formState = MutableStateFlow(AsignaturaFormState())
    val formState: StateFlow<AsignaturaFormState> = _formState.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private var editandoId: String? = null

    fun onNombreChanged(valor: String) {
        _formState.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onCodigoChanged(valor: String) {
        _formState.update { it.copy(codigo = valor, codigoError = null) }
    }

    fun onProfesorChanged(valor: String) {
        _formState.update { it.copy(profesor = valor) }
    }

    fun onSemestreChanged(valor: String) {
        _formState.update { it.copy(semestre = valor) }
    }

    fun cargarParaEditar(asignatura: Asignatura) {
        editandoId = asignatura.id
        _formState.value = AsignaturaFormState(
            nombre = asignatura.nombre,
            codigo = asignatura.codigo,
            profesor = asignatura.profesor,
            semestre = asignatura.semestre
        )
    }

    fun limpiarFormulario() {
        editandoId = null
        _formState.value = AsignaturaFormState()
    }

    fun guardar() {
        if (!validarCampos()) return

        val estado = _formState.value
        val idActual = editandoId ?: java.util.UUID.randomUUID().toString()
        val asignatura = Asignatura(
            id = idActual,
            nombre = estado.nombre.trim(),
            codigo = estado.codigo.trim(),
            profesor = estado.profesor.trim(),
            semestre = estado.semestre.trim()
        )

        TareaRepository.guardarAsignatura(asignatura)
        _formState.update { it.copy(guardadoExitoso = true) }
    }

    fun eliminar(id: String) {
        val exito = TareaRepository.eliminarAsignatura(id)
        if (!exito) {
            _mensajeError.value = "No se puede eliminar: tiene tareas pendientes"
        }
    }

    fun consumeMensajeError() {
        _mensajeError.value = null
    }

    fun consumeGuardadoExitoso() {
        _formState.update { it.copy(guardadoExitoso = false) }
    }

    private fun validarCampos(): Boolean {
        val estado = _formState.value
        var esValido = true

        val nombreError = if (estado.nombre.isBlank()) "El nombre es obligatorio" else null
        if (nombreError != null) esValido = false

        val codigoError = if (estado.codigo.isBlank()) "El código es obligatorio" else null
        if (codigoError != null) esValido = false

        _formState.update { it.copy(nombreError = nombreError, codigoError = codigoError) }
        return esValido
    }
}