package com.example.socialfit.notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.socialfit.MainActivity
import kotlin.random.Random


class NotificationHandlerNavegacion(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val channelId = "notification_channel_id" // ID único (como dirección)

    fun showSimpleNotification(destino: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destino", destino) // Ventana a la que se irá al hacer clic en la notificación
        }
        val notificationId = Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("") // Título
            .setContentText("") // Texto/cuerpo de la notificación
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono del sistema
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Tipo de prioridad
            .setAutoCancel(true) // Que la notificación se descarte al tocarla
            .setContentIntent(pendingIntent)
            .build() // Crea objeto final
        notificationManager.notify(notificationId, notification) // Envía la notificación
    }
}
