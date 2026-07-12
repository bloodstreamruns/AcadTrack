package com.example.acadtrack_beta.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SesionRepository {

    private val _emailUsuario = MutableStateFlow<String?>(null)
    val emailUsuario: StateFlow<String?> = _emailUsuario.asStateFlow()

    fun iniciarSesion(email: String) {
        _emailUsuario.value = email
    }

    fun cerrarSesion() {
        _emailUsuario.value = null
    }
}