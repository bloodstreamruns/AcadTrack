package com.example.acadtrack_beta.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acadtrack_beta.data.repository.SesionRepository
import com.example.acadtrack_beta.data.repository.TareaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PerfilUiState(
    val email: String = "",
    val totalAsignaturas: Int = 0,
    val totalTareas: Int = 0,
    val tareasCompletadas: Int = 0
) {
    val inicial: String
        get() = email.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}

class PerfilViewModel : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = combine(
        SesionRepository.emailUsuario,
        TareaRepository.asignaturas,
        TareaRepository.tareas
    ) { email, asignaturas, tareas ->
        PerfilUiState(
            email = email ?: "",
            totalAsignaturas = asignaturas.size,
            totalTareas = tareas.size,
            tareasCompletadas = tareas.count { it.completada }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState()
    )

    fun cerrarSesion() {
        SesionRepository.cerrarSesion()
    }
}