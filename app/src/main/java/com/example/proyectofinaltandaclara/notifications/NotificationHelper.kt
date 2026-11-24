package com.example.proyectofinaltandaclara.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.proyectofinaltandaclara.R

/**
 * Helper para crear el canal de notificaciones locales y emitir avisos de cobranza.
 */
object NotificationHelper {

    const val CHANNEL_ID = "tanda_clara_channel"
    private const val CHANNEL_NAME = "Recordatorios de Tandas"
    private const val CHANNEL_DESC = "Avisos sobre los vencimientos y pagos pendientes de las tandas."

    fun crearCanalNotificaciones(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    @SuppressLint("MissingPermission")
    fun enviarNotificacion(context: Context, id: Int, titulo: String, mensaje: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: Exception) {
            // El usuario no ha otorgado el permiso POST_NOTIFICATIONS
        }
    }
}
