package com.example.acadtrack_beta.data.local

import androidx.room.TypeConverter
import com.example.acadtrack_beta.data.model.Prioridad
import com.example.acadtrack_beta.data.model.TipoTarea
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun fromEpochMilli(valor: Long?): LocalDateTime? =
        valor?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC) }

    @TypeConverter
    fun toEpochMilli(fecha: LocalDateTime?): Long? =
        fecha?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun fromTipoTareaNombre(valor: String?): TipoTarea? = valor?.let { TipoTarea.valueOf(it) }

    @TypeConverter
    fun tipoTareaANombre(tipo: TipoTarea?): String? = tipo?.name

    @TypeConverter
    fun fromPrioridadNombre(valor: String?): Prioridad? = valor?.let { Prioridad.valueOf(it) }

    @TypeConverter
    fun prioridadANombre(prioridad: Prioridad?): String? = prioridad?.name
}