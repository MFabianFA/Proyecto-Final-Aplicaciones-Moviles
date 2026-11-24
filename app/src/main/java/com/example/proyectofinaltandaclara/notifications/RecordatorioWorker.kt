package com.example.proyectofinaltandaclara.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectofinaltandaclara.TandaApplication
import com.example.proyectofinaltandaclara.utils.CalculadoraTanda
import java.time.LocalDate

/**
 * Worker ejecutado periódicamente por WorkManager para verificar si mañana vence un periodo
 * de alguna tanda y faltan pagos por realizar. Emitirá la notificación requerida.
 */
class RecordatorioWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = appContext.applicationContext as? TandaApplication ?: return Result.failure()
        val repository = app.repository

        try {
            val manana = LocalDate.now().plusDays(1)
            val tandas = repository.getTodasLasTandasDirecto()

            for (tanda in tandas) {
                val participantes = repository.getParticipantesPorTandaDirecto(tanda.id)
                val nPart = participantes.size
                if (nPart == 0) continue

                val pagos = repository.getPagosPorTandaDirecto(tanda.id)

                for (p in 1..nPart) {
                    val fechaP = CalculadoraTanda.calcularFechaPeriodo(tanda.fechaInicio, p, tanda.periodicidad)
                    if (fechaP == manana) {
                        val pagosEnP = pagos.filter { it.periodo == p }
                        val nFaltantes = nPart - pagosEnP.size
                        if (nFaltantes > 0) {
                            val titulo = "Recordatorio de Tanda"
                            val mensaje = "Mañana vence el pago de la tanda ${tanda.nombre}. Faltan $nFaltantes personas."
                            NotificationHelper.enviarNotificacion(
                                context = appContext,
                                id = (tanda.id * 100 + p).toInt(),
                                titulo = titulo,
                                mensaje = mensaje
                            )
                        }
                    }
                }
            }
            return Result.success()
        } catch (_: Exception) {
            return Result.retry()
        }
    }
}
