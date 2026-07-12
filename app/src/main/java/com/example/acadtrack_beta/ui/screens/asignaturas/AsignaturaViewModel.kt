package com.example.acadtrack_beta.ui.screens.asignaturas

import androidx.lifecycle.ViewModel
import com.example.acadtrack_beta.data.model.Asignatura
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Almacenamiento en memoria por ahora (Room se aplica más adelante, Tema 6)
class AsignaturaViewModel : ViewModel() {

    private val _asignaturas = MutableStateFlow<List<Asignatura>>(emptyList())
    val asignaturas: StateFlow<List<Asignatura>> = _asignaturas.asStateFlow()

    private val _formState = MutableStateFlow(AsignaturaFormState())
    val formState: StateFlow<AsignaturaFormState> = _formState.asStateFlow()

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

        _asignaturas.update { lista ->
            if (editandoId != null) {
                lista.map { if (it.id == editandoId) asignatura else it }
            } else {
                lista + asignatura
            }
        }

        _formState.update { it.copy(guardadoExitoso = true) }
    }

    fun eliminar(id: String) {
        _asignaturas.update { lista -> lista.filterNot { it.id == id } }
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