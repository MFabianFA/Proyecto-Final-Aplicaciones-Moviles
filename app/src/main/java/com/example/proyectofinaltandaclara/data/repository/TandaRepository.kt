package com.example.proyectofinaltandaclara.data.repository

import com.example.proyectofinaltandaclara.data.local.PagoDao
import com.example.proyectofinaltandaclara.data.local.ParticipanteDao
import com.example.proyectofinaltandaclara.data.local.TandaDao
import com.example.proyectofinaltandaclara.data.model.Pago
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.data.model.Tanda
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repositorio para gestionar las operaciones con el origen de datos local (Room).
 */
class TandaRepository(
    private val tandaDao: TandaDao,
    private val participanteDao: ParticipanteDao,
    private val pagoDao: PagoDao
) {
    // --- TANDAS ---
    val todasLasTandas: Flow<List<Tanda>> = tandaDao.getTodasLasTandas()

    suspend fun getTodasLasTandasDirecto(): List<Tanda> = tandaDao.getTodasLasTandasDirecto()

    fun getTandaPorId(tandaId: Long): Flow<Tanda?> = tandaDao.getTandaPorId(tandaId)

    suspend fun getTandaPorIdDirecto(tandaId: Long): Tanda? = tandaDao.getTandaPorIdDirecto(tandaId)

    suspend fun insertarTanda(tanda: Tanda): Long = tandaDao.insertarTanda(tanda)

    suspend fun actualizarTanda(tanda: Tanda) = tandaDao.actualizarTanda(tanda)

    suspend fun eliminarTanda(tanda: Tanda) = tandaDao.eliminarTanda(tanda)

    suspend fun eliminarTandaPorId(tandaId: Long) = tandaDao.eliminarTandaPorId(tandaId)

    // --- PARTICIPANTES ---
    fun getParticipantesPorTanda(tandaId: Long): Flow<List<Participante>> =
        participanteDao.getParticipantesPorTanda(tandaId)

    suspend fun getParticipantesPorTandaDirecto(tandaId: Long): List<Participante> =
        participanteDao.getParticipantesPorTandaDirecto(tandaId)

    suspend fun insertarParticipante(participante: Participante): Long =
        participanteDao.insertarParticipante(participante)

    suspend fun insertarParticipantes(participantes: List<Participante>): List<Long> =
        participanteDao.insertarParticipantes(participantes)

    suspend fun eliminarParticipante(participante: Participante) =
        participanteDao.eliminarParticipante(participante)

    /**
     * Realiza el sorteo de turnos: revuelve aleatoriamente la lista de participantes
     * y asigna turnos del 1 a N.
     */
    suspend fun realizarSorteoDeTurnos(tandaId: Long) {
        val listaActual = participanteDao.getParticipantesPorTandaDirecto(tandaId)
        if (listaActual.isEmpty()) return

        val listaSorteada = listaActual.shuffled().mapIndexed { index, participante ->
            participante.copy(turno = index + 1)
        }
        participanteDao.actualizarParticipantes(listaSorteada)
    }

    // --- PAGOS ---
    fun getPagosPorTanda(tandaId: Long): Flow<List<Pago>> =
        pagoDao.getPagosPorTanda(tandaId)

    suspend fun getPagosPorTandaDirecto(tandaId: Long): List<Pago> =
        pagoDao.getPagosPorTandaDirecto(tandaId)

    suspend fun getTodosLosPagosDirecto(): List<Pago> =
        pagoDao.getTodosLosPagosDirecto()

    suspend fun registrarPago(
        tandaId: Long,
        participanteId: Long,
        periodo: Int,
        fechaPago: LocalDate = LocalDate.now(),
        nota: String? = null
    ): Long {
        val pago = Pago(
            tandaId = tandaId,
            participanteId = participanteId,
            periodo = periodo,
            fechaPago = fechaPago,
            nota = nota
        )
        return pagoDao.insertarPago(pago)
    }

    suspend fun eliminarPago(pago: Pago) = pagoDao.eliminarPago(pago)

    suspend fun eliminarPagoEspecifico(tandaId: Long, participanteId: Long, periodo: Int) =
        pagoDao.eliminarPagoEspecifico(tandaId, participanteId, periodo)
}
