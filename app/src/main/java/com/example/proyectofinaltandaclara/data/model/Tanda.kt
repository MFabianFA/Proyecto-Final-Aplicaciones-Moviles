package com.example.proyectofinaltandaclara.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad que representa una Tanda organizada.
 *
 * @property id Identificador único autogenerado por Room.
 * @property nombre Nombre descriptivo de la tanda.
 * @property montoPorPersona Aporte fijo individual en pesos por cada periodo.
 * @property periodicidad Frecuencia de la tanda (SEMANAL, QUINCENAL, MENSUAL).
 * @property fechaInicio Fecha en que inicia el primer periodo de cobro/pago.
 * @property fechaCreacion Fecha en que se registró la tanda en el sistema.
 */
@Entity(tableName = "tandas")
data class Tanda(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val montoPorPersona: Double,
    val periodicidad: Periodicidad,
    val fechaInicio: LocalDate,
    val fechaCreacion: LocalDate = LocalDate.now()
)
