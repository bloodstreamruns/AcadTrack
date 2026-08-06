package com.example.acadtrack_beta.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acadtrack_beta.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onEmailChanged(v: String) = _uiState.update { it.copy(email = v, emailError = null, generalError = null) }
    fun onPasswordChanged(v: String) = _uiState.update { it.copy(password = v, passwordError = null, generalError = null) }
    fun onConfirmarChanged(v: String) = _uiState.update { it.copy(confirmarPassword = v, confirmarError = null) }

    fun onRegistrarClicked() {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            AuthRepository.registrar(_uiState.value.email.trim(), _uiState.value.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isRegistroExitoso = true) }
                }
                .onFailure { error ->
                    val mensaje = when (error) {
                        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
                        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil"
                        else -> "Error de conexión. Intenta de nuevo."
                    }
                    _uiState.update { it.copy(isLoading = false, generalError = mensaje) }
                }
        }
    }

    fun consumeRegistroExitoso() = _uiState.update { it.copy(isRegistroExitoso = false) }

    private fun validar(): Boolean {
        val s = _uiState.value
        var valido = true

        val emailError = when {
            s.email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "Correo inválido"
            else -> null
        }
        if (emailError != null) valido = false

        val passwordError = when {
            s.password.isBlank() -> "La contraseña es obligatoria"
            s.password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
        if (passwordError != null) valido = false

        val confirmarError = if (s.confirmarPassword != s.password) "Las contraseñas no coinciden" else null
        if (confirmarError != null) valido = false

        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError, confirmarError = confirmarError) }
        return valido
    }
}

