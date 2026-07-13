package com.example.acadtrack_beta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.acadtrack_beta.data.model.Asignatura
import com.example.acadtrack_beta.data.model.Tarea

@Database(entities = [Asignatura::class, Tarea::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun asignaturaDao(): AsignaturaDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile private var INSTANCIA: AppDatabase? = null

        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "acadtrack.db"
                )
                    // Si cambias los campos de Asignatura/Tarea más adelante sin escribir
                    // una migración, esto borra y recrea las tablas en vez de crashear.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}