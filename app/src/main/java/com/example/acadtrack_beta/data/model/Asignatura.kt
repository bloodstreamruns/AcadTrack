package com.example.acadtrack_beta.data.model

import java.util.UUID

data class Asignatura(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val codigo: String,
    val profesor: String = "",
    val semestre: String = "2026-2"
)