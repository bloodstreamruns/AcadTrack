package com.example.acadtrack_beta.ui.screens.login

data class RegistroUiState(
    val email: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmarError: String? = null,
    val generalError: String? = null,
    val isRegistroExitoso: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && confirmarPassword.isNotBlank() &&
                emailError == null && passwordError == null && confirmarError == null
}

