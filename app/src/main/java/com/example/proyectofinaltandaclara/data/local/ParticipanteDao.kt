package com.example.proyectofinaltandaclara.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinaltandaclara.data.model.Participante
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [Participante].
 */
@Dao
interface ParticipanteDao {

    @Query("SELECT * FROM participantes WHERE tandaId = :tandaId ORDER BY turno ASC")
    fun getParticipantesPorTanda(tandaId: Long): Flow<List<Participante>>

    @Query("SELECT * FROM participantes WHERE tandaId = :tandaId ORDER BY turno ASC")
    suspend fun getParticipantesPorTandaDirecto(tandaId: Long): List<Participante>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarParticipante(participante: Participante): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarParticipantes(participantes: List<Participante>): List<Long>

    @Update
    suspend fun actualizarParticipantes(participantes: List<Participante>)

    @Delete
    suspend fun eliminarParticipante(participante: Participante)

    @Query("DELETE FROM participantes WHERE tandaId = :tandaId")
    suspend fun eliminarParticipantesDeTanda(tandaId: Long)
}
