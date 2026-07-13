package com.example.acadtrack_beta.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "asignaturas")
data class Asignatura(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val codigo: String,
    val profesor: String = "",
    val semestre: String = "2026-2"
)