package com.example.socialfit

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object  FirebaseTemplate {

    // Centralizamos las instancias (una sola para cada servicio)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    // --- AUTENTICACIÓN ---

    suspend fun registrarUsuario(email: String, pass: String): Result<String> {
        return try {
            auth.signOut()
            // Usamos .await() para que el código se "pare" aquí hasta que Firebase responda
            val resultado = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = resultado.user?.uid ?: ""
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun iniciarSesion(email: String, contrasena: String) {
        auth.signOut()
        auth.signInWithEmailAndPassword(email.trim(), contrasena.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Login exitoso")
                } else {
                    Log.e("Firebase", "Error en login: ${task.exception?.message}")
                }
            }
    }

    // --- FIRESTORE CRUD ---

    fun insertarUsuario(uid: String, data: Map<String, Any>) {
        db.collection("usuarios").document(uid).set(data)
            .addOnSuccessListener { Log.d("Firestore", "Insertado correctamente") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error: ${e.message}") }
    }

    fun obtenerProductosActual(email: String?, onResult: (Map<String, Any>?) -> Unit) {
        if(!email?.isEmpty()!!){
            db.collection("producto").document(email).get()
                .addOnSuccessListener { onResult(it.data) }
                .addOnFailureListener { onResult(null) }
        }
    }

    fun editarUsuario(data: Map<String, Any>) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).set(data)
            .addOnSuccessListener { Log.d("Firestore", "Actualizado") }
    }

    fun eliminarDocumento(documentoId: String) {
        db.collection("usuarios").document(documentoId).delete()
            .addOnSuccessListener { Log.d("Firestore", "Eliminado") }
    }

    // --- CONSULTAS Y ÚTILES ---

    fun consultarPorCampo(onResult: (List<Any>) -> Unit) {
        val uidActual = auth.currentUser?.uid ?: return
        db.collection("usuarios")
            .whereEqualTo("uid", uidActual)
            .get()
            .addOnSuccessListener { onResult(it.documents) }
    }

    fun generarIdAutomatico(): String = db.collection("usuarios").document().id

    fun obtenerIdUsuarioActual(): String? = auth.currentUser?.uid

    // --- VERIFICACIÓN DE EMAIL (SUSPENDIDAS PARA CORRUTINAS) ---

    // 1. Envía el correo
    suspend fun enviarEmailVerificacion(): Result<Unit> {
        return try {
            val usuario = auth.currentUser ?: throw Exception("No hay usuario autenticado")
            val usuar = auth.currentUser
            Log.d("DEBUG_EMAIL", "Intentando enviar a: ${usuar?.email} con UID: ${usuar?.uid}")
            usuario.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Comprueba el estado y actualiza Firestore si es necesario
    suspend fun verificarYActualizarEstado(email: String?, idUsuario:String): Result<Boolean> {
        return try {
            val usuario = auth.currentUser ?: throw Exception("Sesión expirada")

            // Recargamos el usuario para ver si ya hizo click en el link del email
            usuario.reload().await()
            val verificado = usuario.isEmailVerified

            if (verificado && !email?.isEmpty()!!) {
                // Si está verificado, actualizamos su campo en Firestore
                db.collection("usuario").document(idUsuario)
                    .update("verificado", true).await()
            }

            Result.success(verificado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------- CAMBIO DE CONTRASEÑA ----------
    // Envía un email para restablecer la contraseña
    suspend fun enviarEmailCambioContrasena(email: String): Result<Unit> {
        return try {
            // sendPasswordResetEmail -> Firebase envía email con enlace de cambio
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al enviar email de recuperación: ${e.message}"))
        }
    }

    suspend fun comprobarSiEmailExiste(email: String): Boolean {
        return try {
            // Hacemos una consulta buscando documentos donde el campo "email" sea igual al que recibimos
            val resultado = db.collection("usuario")
                .whereEqualTo("email", email)
                .get()
                .await()

            // Si el resultado no está vacío, significa que ya existe al menos un usuario con ese email
            !resultado.isEmpty
        } catch (e: Exception) {
            // Manejar el error (por ejemplo, falta de conexión)
            Log.e("FirestoreError", "Error al comprobar email", e)
            false
        }
    }

    // Cambiamos el retorno a String? (opcional)
    suspend fun obtenerIdConEmail(email: String) : String? {
        return try {
            val querySnapshot = db.collection("usuario")
                .whereEqualTo("email", email.trim())
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].id // Retorna el ID si lo encuentra
            } else {
                null // Retorna nulo si el email no existe
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error: ${e.message}")
            null // Retorna nulo si hay un error de conexión
        }
    }

    suspend fun obtenerDescripcion(id: String): String? {
        return try {
            // Usamos await() para esperar la respuesta de forma segura
            val document = db.collection("usuario").document(id).get().await()

            if (document.exists()) {
                // Extraemos el campo "descripcion"
                document.getString("descripcion")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener descripción: ${e.message}")
            null
        }
    }

}