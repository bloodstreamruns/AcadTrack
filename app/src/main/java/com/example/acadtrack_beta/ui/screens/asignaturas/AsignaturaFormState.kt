package com.example.acadtrack_beta.ui.screens.asignaturas

// Estado del formulario: mismo patrón que LoginUiState
data class AsignaturaFormState(
    val nombre: String = "",
    val codigo: String = "",
    val profesor: String = "",
    val semestre: String = "2026-2",
    val nombreError: String? = null,
    val codigoError: String? = null,
    val isSaving: Boolean = false,
    val guardadoExitoso: Boolean = false
) {
    val isFormValid: Boolean
        get() = nombre.isNotBlank() && codigo.isNotBlank() &&
                nombreError == null && codigoError == null
}