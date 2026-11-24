package com.example.proyectofinaltandaclara

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.proyectofinaltandaclara.data.local.TandaDatabase
import com.example.proyectofinaltandaclara.data.repository.TandaRepository
import com.example.proyectofinaltandaclara.notifications.NotificationHelper
import com.example.proyectofinaltandaclara.notifications.RecordatorioWorker
import java.util.concurrent.TimeUnit

/**
 * Clase Application para inicializar la base de datos, el repositorio,
 * el canal de notificaciones y la tarea periódica con WorkManager.
 */
class TandaApplication : Application() {

    val database: TandaDatabase by lazy {
        TandaDatabase.getDatabase(this)
    }

    val repository: TandaRepository by lazy {
        TandaRepository(
            tandaDao = database.tandaDao(),
            participanteDao = database.participanteDao(),
            pagoDao = database.pagoDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.crearCanalNotificaciones(this)
        programarRecordatorioDiario()
    }

    private fun programarRecordatorioDiario() {
        val workRequest = PeriodicWorkRequestBuilder<RecordatorioWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "recordatorio_tanda_diario",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
