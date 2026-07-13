package com.example.acadtrack_beta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.acadtrack_beta.data.model.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY fechaEntrega ASC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOActualizar(tarea: Tarea)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun eliminarPorId(id: String)
}