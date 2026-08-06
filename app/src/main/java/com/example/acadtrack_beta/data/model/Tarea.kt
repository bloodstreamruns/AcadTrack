package com.example.acadtrack_beta.data.model

import java.time.LocalDateTime
import java.util.UUID

enum class TipoTarea {
    EXAMEN, TAREA, PROYECTO, LECTURA, LABORATORIO, OTRO
}

enum class Prioridad {
    ALTA, MEDIA, BAJA
}

data class Tarea(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val asignaturaId: String = "",
    val fechaEntrega: LocalDateTime = LocalDateTime.now(),
    val tipo: TipoTarea = TipoTarea.TAREA,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val completada: Boolean = false,
    val notas: String = ""
)