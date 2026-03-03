package com.example.socialfit.notificacion

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val channelId = "notification_channel_id" // ID único (como dirección)
    fun showSimpleNotification() {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("") // Título
            .setContentText("") // Texto/cuerpo de la notificación
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono del sistema
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Tipo de prioridad
            .setAutoCancel(true) // Que la notificación se descarte al tocarla
            .build() // Crea objeto final
        notificationManager.notify(Random.nextInt(), notification) // Envía la notificación
    }
}
