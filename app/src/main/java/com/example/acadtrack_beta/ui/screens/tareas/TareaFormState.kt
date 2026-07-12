package com.example.acadtrack_beta.ui.screens.tareas

import com.example.acadtrack_beta.data.model.Prioridad
import com.example.acadtrack_beta.data.model.TipoTarea
import java.time.LocalDate

// Nota: el DatePicker de Material 3 solo captura fecha (sin hora).
// Al guardar, se asume 23:59 como hora límite del día de entrega.
data class TareaFormState(
    val titulo: String = "",
    val descripcion: String = "",
    val asignaturaId: String? = null,
    val asignaturaNombre: String = "",
    val fechaEntrega: LocalDate? = null,
    val tipo: TipoTarea = TipoTarea.TAREA,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val notas: String = "",
    val tituloError: String? = null,
    val asignaturaError: String? = null,
    val fechaError: String? = null,
    val guardadoExitoso: Boolean = false
) {
    val isFormValid: Boolean
        get() = titulo.isNotBlank() && asignaturaId != null && fechaEntrega != null &&
                tituloError == null && asignaturaError == null && fechaError == null
}

