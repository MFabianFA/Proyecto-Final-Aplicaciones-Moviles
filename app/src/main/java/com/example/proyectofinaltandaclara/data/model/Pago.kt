package com.example.proyectofinaltandaclara.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad que registra la aportación de un participante para un periodo específico.
 *
 * @property id Identificador único del pago.
 * @property tandaId ID de la tanda asociada.
 * @property participanteId ID del participante que realizó la aportación.
 * @property periodo Número del periodo correspondiente (1..N).
 * @property fechaPago Fecha en que se realizó o registró el pago.
 * @property nota Comentario u observación opcional sobre el pago.
 */
@Entity(
    tableName = "pagos",
    foreignKeys = [
        ForeignKey(
            entity = Tanda::class,
            parentColumns = ["id"],
            childColumns = ["tandaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Participante::class,
            parentColumns = ["id"],
            childColumns = ["participanteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tandaId"]),
        Index(value = ["participanteId"]),
        Index(value = ["tandaId", "participanteId", "periodo"], unique = true)
    ]
)
data class Pago(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tandaId: Long,
    val participanteId: Long,
    val periodo: Int,
    val fechaPago: LocalDate,
    val nota: String? = null
)
