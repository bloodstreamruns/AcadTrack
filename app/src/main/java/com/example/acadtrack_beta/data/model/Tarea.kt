package com.example.acadtrack_beta.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

enum class TipoTarea {
    EXAMEN, TAREA, PROYECTO, LECTURA, LABORATORIO, OTRO
}

enum class Prioridad {
    ALTA, MEDIA, BAJA
}

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val descripcion: String = "",
    val asignaturaId: String,
    val fechaEntrega: LocalDateTime,
    val tipo: TipoTarea = TipoTarea.TAREA,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val completada: Boolean = false,
    val notas: String = ""
)