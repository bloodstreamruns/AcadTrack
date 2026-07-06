package com.example.acadtrack_beta.ui.screens.login
//modelo login
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                generalError = null
            )
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClicked() {
        if (!validateFields()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            try {

                delay(1200)

                val loginExitoso = true

                if (loginExitoso) {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = "Correo o contraseña incorrectos"
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = "Error de conexión. Intenta de nuevo."
                    )
                }
            }
        }
    }

    fun consumeLoginSuccess() {
        _uiState.update { it.copy(isLoginSuccessful = false) }
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        var isValid = true

        val emailError = when {
            state.email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() ->
                "Correo inválido"
            else -> null
        }
        if (emailError != null) isValid = false

        val passwordError = when {
            state.password.isBlank() -> "La contraseña es obligatoria"
            state.password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
        if (passwordError != null) isValid = false

        _uiState.update {
            it.copy(emailError = emailError, passwordError = passwordError)
        }

        return isValid
    }
}