package com.example.proyectofinaltandaclara.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinaltandaclara.data.model.Pago
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [Pago].
 */
@Dao
interface PagoDao {

    @Query("SELECT * FROM pagos WHERE tandaId = :tandaId")
    fun getPagosPorTanda(tandaId: Long): Flow<List<Pago>>

    @Query("SELECT * FROM pagos WHERE tandaId = :tandaId")
    suspend fun getPagosPorTandaDirecto(tandaId: Long): List<Pago>

    @Query("SELECT * FROM pagos WHERE tandaId = :tandaId AND participanteId = :participanteId AND periodo = :periodo")
    suspend fun getPago(tandaId: Long, participanteId: Long, periodo: Int): Pago?

    @Query("SELECT * FROM pagos")
    suspend fun getTodosLosPagosDirecto(): List<Pago>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPago(pago: Pago): Long

    @Delete
    suspend fun eliminarPago(pago: Pago)

    @Query("DELETE FROM pagos WHERE tandaId = :tandaId AND participanteId = :participanteId AND periodo = :periodo")
    suspend fun eliminarPagoEspecifico(tandaId: Long, participanteId: Long, periodo: Int)
}
