package com.example.acadtrack_beta.ui.screens.asignaturas

import androidx.lifecycle.ViewModel
import com.example.acadtrack_beta.data.model.Asignatura
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class AsignaturaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AsignaturaUiState())
    val uiState: StateFlow<AsignaturaUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AsignaturaFormUiState())
    val formState: StateFlow<AsignaturaFormUiState> = _formState.asStateFlow()

    // ---------- Listado ----------

    fun onEliminarClicked(asignatura: Asignatura) {
        _uiState.update { it.copy(asignaturaAEliminar = asignatura) }
    }

    fun onCancelarEliminar() {
        _uiState.update { it.copy(asignaturaAEliminar = null) }
    }

    fun onConfirmarEliminar() {
        val asignatura = _uiState.value.asignaturaAEliminar ?: return
        _uiState.update { state ->
            state.copy(
                asignaturas = state.asignaturas.filterNot { it.id == asignatura.id },
                asignaturaAEliminar = null
            )
        }
        // TODO: eliminar también de la fuente de datos persistente (Room/Firebase/etc.)
    }

    // ---------- Formulario ----------

    fun onNuevaAsignatura() {
        _formState.value = AsignaturaFormUiState()
    }

    fun onEditarAsignatura(id: String) {
        val existente = _uiState.value.asignaturas.find { it.id == id }
        _formState.value = if (existente != null) {
            AsignaturaFormUiState(
                id = existente.id,
                nombre = existente.nombre,
                codigo = existente.codigo,
                profesor = existente.profesor,
                semestre = existente.semestre
            )
        } else {
            AsignaturaFormUiState()
        }
    }

    fun onNombreChanged(value: String) {
        _formState.update {
            it.copy(
                nombre = value,
                nombreError = if (value.isBlank()) "El nombre es obligatorio" else null
            )
        }
    }

    fun onCodigoChanged(value: String) {
        _formState.update {
            it.copy(
                codigo = value,
                codigoError = if (value.isBlank()) "El código es obligatorio" else null
            )
        }
    }

    fun onProfesorChanged(value: String) {
        _formState.update { it.copy(profesor = value) }
    }

    fun onSemestreChanged(value: String) {
        _formState.update { it.copy(semestre = value) }
    }

    fun onGuardarClicked() {
        val form = _formState.value
        val nombreError = if (form.nombre.isBlank()) "El nombre es obligatorio" else null
        val codigoError = if (form.codigo.isBlank()) "El código es obligatorio" else null

        if (nombreError != null || codigoError != null) {
            _formState.update { it.copy(nombreError = nombreError, codigoError = codigoError) }
            return
        }

        _uiState.update { state ->
            val listaActualizada = if (form.isEditando) {
                state.asignaturas.map { asignatura ->
                    if (asignatura.id == form.id) {
                        asignatura.copy(
                            nombre = form.nombre,
                            codigo = form.codigo,
                            profesor = form.profesor,
                            semestre = form.semestre
                        )
                    } else {
                        asignatura
                    }
                }
            } else {
                state.asignaturas + Asignatura(
                    id = UUID.randomUUID().toString(),
                    nombre = form.nombre,
                    codigo = form.codigo,
                    profesor = form.profesor,
                    semestre = form.semestre
                )
            }
            state.copy(asignaturas = listaActualizada)
        }
        // TODO: persistir cambios en la fuente de datos real (Room/Firebase/etc.)

        _formState.update { it.copy(isGuardadoExitoso = true) }
    }

    fun consumeGuardadoExitoso() {
        _formState.update { it.copy(isGuardadoExitoso = false) }
    }
}