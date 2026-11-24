package com.example.proyectofinaltandaclara.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinaltandaclara.data.model.Tanda
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [Tanda].
 */
@Dao
interface TandaDao {

    @Query("SELECT * FROM tandas ORDER BY fechaCreacion DESC")
    fun getTodasLasTandas(): Flow<List<Tanda>>

    @Query("SELECT * FROM tandas ORDER BY fechaCreacion DESC")
    suspend fun getTodasLasTandasDirecto(): List<Tanda>

    @Query("SELECT * FROM tandas WHERE id = :id")
    fun getTandaPorId(id: Long): Flow<Tanda?>

    @Query("SELECT * FROM tandas WHERE id = :id")
    suspend fun getTandaPorIdDirecto(id: Long): Tanda?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTanda(tanda: Tanda): Long

    @Update
    suspend fun actualizarTanda(tanda: Tanda)

    @Delete
    suspend fun eliminarTanda(tanda: Tanda)

    @Query("DELETE FROM tandas WHERE id = :id")
    suspend fun eliminarTandaPorId(id: Long)
}
