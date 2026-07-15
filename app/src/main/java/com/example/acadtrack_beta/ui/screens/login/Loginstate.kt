package com.example.acadtrack_beta.ui.screens.login
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoginSuccessful: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                emailError == null && passwordError == null
}