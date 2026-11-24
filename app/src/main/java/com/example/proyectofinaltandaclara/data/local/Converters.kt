package com.example.proyectofinaltandaclara.data.local

import androidx.room.TypeConverter
import com.example.proyectofinaltandaclara.data.model.Periodicidad
import java.time.LocalDate

/**
 * Conversores de tipos para Room: almacena objetos LocalDate como String ISO y Periodicidad como su nombre Enum.
 */
class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromPeriodicidad(value: Periodicidad?): String? {
        return value?.name
    }

    @TypeConverter
    fun toPeriodicidad(value: String?): Periodicidad? {
        return value?.let { Periodicidad.valueOf(it) }
    }
}
