package com.example.socialfit.notificacion

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun GestorNotificaciones(idUsuario: String) {
    // 1. Pedir permiso en Android 13+ (Tiramisu)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            obtenerYGuardarTokenFCM(idUsuario)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            obtenerYGuardarTokenFCM(idUsuario)
        }
    }
}

fun obtenerYGuardarTokenFCM(idUsuario: String) {
    if (idUsuario.isEmpty()) {
        Log.e("NotificacionesFCM", "El idUsuario está vacío, no se puede guardar el token.")
        return
    }

    Log.d("NotificacionesFCM", "Intentando obtener token para el usuario: $idUsuario")

    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.e("NotificacionesFCM", "Fallo al obtener el token de Firebase", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        Log.d("NotificacionesFCM", "¡Token FCM obtenido con éxito!: $token")

        // Guardamos el token en el perfil del usuario usando set con merge por si el documento está vacío
        val db = FirebaseFirestore.getInstance()
        db.collection("usuario").document(idUsuario)
            .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Log.d("NotificacionesFCM", "¡Token guardado en Firestore correctamente!")
            }
            .addOnFailureListener { e ->
                Log.e("NotificacionesFCM", "Error subiendo el token a Firestore", e)
            }
    }
}