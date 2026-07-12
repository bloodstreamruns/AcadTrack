package com.example.acadtrack_beta.data.repository

import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.model.Tarea

class TareaRepository{
    //variables correspondientes a datos guardados en memoria
    private val asignaturas = mutableListOf<Asignatura>()
    private val tareas = mutableListOf<Tarea>()

    //creación del tipo de datos "Asignaturas"
    fun getAllAsignaturas(): List<Asignatura> = asignaturas.toList()
}