package com.example.socialfit.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.example.socialfit.FirebaseTemplate
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavController) {
    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // Acento (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFF0F2F5) // Fondo de la pantalla (Evita el blanco puro)
    val SurfaceWhite = Color(0xFFFFFFFF)       // Fondo de tarjetas o TextFields

    // Variables de los datos recogidos en los formularios
    var nick by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Hombre") }
    var contrasena = rememberTextFieldState("")
    var passVisible by remember { mutableStateOf(false) }
    val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") // Patrón a seguir en el email

    // Se obtiene el contexto actual, necesario para construir la base de datos Room y mostrar mensajes Toasts.
    val context = LocalContext.current

    //Conexion con FirebaseFirestore
    val dbfire = FirebaseFirestore.getInstance()

    val scope = rememberCoroutineScope()

    // BARRA SUPERIOR
    Scaffold(
        // Cabecera de la pantalla
        topBar = {
            TopAppBar(
                modifier = Modifier.height(60.dp),
                title = {
                    Text(text = "Registrar un usuario", fontSize = 15.sp)
                },
                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White
                )
            )
        }) { innerPadding ->
        // Ordenamos de manera vertical los campos a rellenar del usuario
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pedimos el nombre
            OutlinedTextField(
                value = nick,
                onValueChange = {
                    if(it.length < 40) {
                        nick = it
                    }},
                label = { Text("Nick de usuario") },
                modifier = Modifier.width(300.dp)
            )
            //Pedimos los apellidos
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    if(it.length < 50) {
                        nombre = it
                    }},
                label = { Text("Nombre del usuario") },
                modifier = Modifier.width(300.dp)
            )
            // Pedimos el email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    if(it.length < 40) {
                        email = it
                    }},
                label = { Text("Email") },
                modifier = Modifier.width(300.dp),

                )
            //Pedimos la contraseña
            OutlinedSecureTextField(
                state = contrasena,
                label = { Text("Contraseña") },
                modifier = Modifier
                    .width(300.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector = if (passVisible) Lucide.EyeOff else Lucide.Eye,
                            contentDescription = if (passVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                textObfuscationMode = if (passVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped
            )
            Spacer(Modifier.height(10.dp))
            Text(text = "Sexo:", modifier = Modifier.width(300.dp), fontSize = 14.sp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(300.dp)
            ) {
                // Opción Hombre
                RadioButton(
                    selected = sexo == "Hombre",
                    onClick = { sexo = "Hombre" }
                )
                Text(
                    text = "Hombre",
                    modifier = Modifier.clickable { sexo = "Hombre" }.padding(end = 16.dp)
                )

                // Opción Mujer
                RadioButton(
                    selected = sexo == "Mujer",
                    onClick = { sexo = "Mujer" }
                )
                Text(
                    text = "Mujer",
                    modifier = Modifier.clickable { sexo = "Mujer" }
                )
            }
            Spacer(Modifier.size(20.dp)) // Espacio para que el boton no quede pegado a los formularios

            // Boton para añadir usuario
            Button(
                onClick = {
                    // Validaciones básicas de campos
                    scope.launch{
                        when {
                            nick.isBlank() -> {
                                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                            }
                            nombre.isBlank() -> {
                                Toast.makeText(context, "Los apellidos no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                            }
                            email.isBlank() -> {
                                Toast.makeText(context, "El email no puede estar vacío", Toast.LENGTH_SHORT).show()
                            }
                            !email.matches(emailPattern) -> {
                                Toast.makeText(context, "El email no tiene un formato válido", Toast.LENGTH_SHORT).show()
                            }
                            contrasena.text.length < 8 -> {
                                Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                            }
                            FirebaseTemplate.comprobarSiEmailExiste(email) -> {
                                Toast.makeText(context, "Ya existe alguien registrado con ese email", Toast.LENGTH_SHORT).show()
                            }
                            // Si ningula de las condidiones anteriores se ha cumpplido el usuario es
                            // valido y lo añadimos
                            else -> {

                                    try {
                                        // 1. Crear el usuario en Firebase Auth (INDISPENSABLE para el email)
                                        // Suponiendo que tienes esta función en tu FirebaseTemplate
                                        val registroAuth = FirebaseTemplate.registrarUsuario(email, contrasena.text.toString())

                                        if (registroAuth.isFailure) {
                                            throw registroAuth.exceptionOrNull() ?: Exception("Error desconocido en Auth")
                                        }

                                        // 2. Guardar en Firestore (usamos el UID del usuario recién creado)
                                        val data = mapOf(
                                            "nombre" to nick,
                                            "apellidos" to nombre,
                                            "email" to email,
                                            "sexo" to sexo,
                                            "password" to contrasena.text,
                                            "verificado" to false // Inicialmente falso
                                        )

                                        // Esto crea el usuario con un ID aleatorio automáticamente
                                        dbfire.collection("usuario").add(data).await()

                                        // 3. ENVIAR EMAIL DE VERIFICACIÓN

                                        val resEmail = FirebaseTemplate.enviarEmailVerificacion()

                                        if (resEmail.isSuccess) {
                                            Toast.makeText(context, "Registro exitoso. Revisa tu email.", Toast.LENGTH_LONG).show()
                                            // Solo navegamos si funciono
                                            navController.navigate(route = AppScreens.InicioSesion.route)
                                        } else {
                                            // ver el error exacto
                                            val errorMsg = resEmail.exceptionOrNull()?.message ?: "Error desconocido"
                                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                            Log.e("FIREBASE_ERROR", "Error al enviar email", resEmail.exceptionOrNull())
                                        }

                                    } catch (e: Exception) {
                                        Log.e("FirebaseError", "ERROR DETALLADO: ", e)
                                        Log.e("FirebaseError", "Error: ${e.message}")
                                        Toast.makeText(context, "Error en el registro: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                            }
                        }
                    }
                    }

            ) {
                Text(text = "Agregar usuario") // Texto dentro del boton
            }

            Button(
                onClick = {
                    // Notificacion flotante al redirigir
                    Toast.makeText(context, "Volviendo a inicio de sesion", Toast.LENGTH_SHORT).show()
                    navController.navigate(route = AppScreens.InicioSesion.route)
                }) {
                Text(text = "Volver a inicio") // Texto dentro del boton
            }
        }
    }
}