package com.example.proyectofinaltandaclara.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un integrante de una tanda específica.
 *
 * @property id Identificador único autogenerado.
 * @property tandaId Clave foránea referenciando a la tanda contenedora (borrado en cascada).
 * @property nombre Nombre completo o apodo del participante.
 * @property telefono Teléfono opcional de contacto.
 * @property turno Número de turno asignado (1..N) para cobrar la tanda reunida.
 */
@Entity(
    tableName = "participantes",
    foreignKeys = [
        ForeignKey(
            entity = Tanda::class,
            parentColumns = ["id"],
            childColumns = ["tandaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tandaId"])]
)
data class Participante(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tandaId: Long,
    val nombre: String,
    val telefono: String? = null,
    val turno: Int
)
