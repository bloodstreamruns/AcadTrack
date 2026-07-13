package com.example.acadtrack_beta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.acadtrack_beta.data.model.Asignatura
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignaturaDao {

    @Query("SELECT * FROM asignaturas ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Asignatura>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOActualizar(asignatura: Asignatura)

    @Query("DELETE FROM asignaturas WHERE id = :id")
    suspend fun eliminarPorId(id: String)
}