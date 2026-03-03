package com.example.socialfit.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.example.socialfit.FirebaseTemplate
import com.example.socialfit.R
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Esta pantalla da acceso al inicio de sesion para continuar con la pantalla de formulario
@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun InicioSesion(navController: NavController){
    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // Acento (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFDDE1E7) // Fondo de la pantalla (Evita el blanco puro)
    val SurfaceWhite = Color(0xFFFFFFFF)       // Fondo de tarjetas o TextFields

    //BBDD Firebase
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var contrasena = rememberTextFieldState("")
    var passVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Image(
        painter = painterResource(R.drawable.fondo),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Scaffold(
        containerColor = BackgroundGrayBlue,
        // Cabecera de la pantalla
        topBar = {
            TopAppBar(modifier = Modifier.height(60.dp),
                title = {
                    Text(text = "Iniciar sesion", fontSize = 15.sp)
                },
                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White)
            )
        }) { innerPadding ->
        // Cuerpo de la pantalla
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            // Mensaje de Bienvenidabasico
            Text(text = "Bienvenid@",
                fontSize = 30.sp,
                color = PurpleDark,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(30.dp)) // Espacio para que no se vea todo tan pegado

            // Campo para rellenar el email
            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGold,      // El borde se vuelve dorado al tocarlo
                    unfocusedBorderColor = PurpleMedium, // Morado suave cuando no se usa
                    focusedLabelColor = PurpleDark,      // El texto de arriba se vuelve morado oscuro
                    cursorColor = AmberGold              // La rayita de escribir es dorada
                ),
                value = email,
                onValueChange = {
                    if(it.length < 30){
                        email = it
                    } },
                label = { Text("Email")},
                modifier = Modifier.width(300.dp)
            )

            Spacer(modifier = Modifier.height(15.dp)) // Espacio para separar los formularios


            // Campo para rellenar la contraseña
            OutlinedSecureTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGold,      // El borde se vuelve dorado al tocarlo
                    unfocusedBorderColor = PurpleMedium, // Morado suave cuando no se usa
                    focusedLabelColor = PurpleDark,      // El texto de arriba se vuelve morado oscuro
                    cursorColor = AmberGold              // La rayita de escribir es dorada
                ),
                state = contrasena,
                label = {Text("Contraseña")},
                modifier = Modifier.width(300.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = {passVisible = !passVisible}) {
                        Icon(
                            tint = AmberGold,
                            imageVector = if (passVisible) Lucide.EyeOff else Lucide.Eye,
                            contentDescription = if(passVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                textObfuscationMode = if(passVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped
            )

            Spacer(Modifier.size(15.dp)) // Espacio para separar el boton de aceptar

            TextButton(onClick = {
                scope.launch {
                    if(email.equals("")){
                        Toast.makeText(context,"Introduce un email",Toast.LENGTH_SHORT).show()
                    }else{
                        val emVer = FirebaseTemplate.verificarYActualizarEstado(email)

                        if(emVer.isSuccess){
                            FirebaseTemplate.enviarEmailCambioContrasena(email)
                            Toast.makeText(context,"Email enviado al correo propuesto",Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(context,"verifica el email primero, mira en spam",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            ) {
                Text("Quiero recuperar mi contraseña")
            }

            Spacer(Modifier.size(15.dp))

            // Boton para enviar la solicitud de inicio de sesion
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDark, // Fondo morado oscuro
                    contentColor = Color.White    // Texto blanco
                ),
                onClick = {
                // Primero comprueba que nada este en blanco porque no tiene sentido
                if(contrasena.text.isBlank() || email.isBlank()) {
                    // Notificacion para avisar de que hay algo vacio
                    Toast.makeText(context,"No puede haber campos en blanco",Toast.LENGTH_SHORT).show()
                }else {


                    // En la variable de la BDFirebase, de la coleccion usuario, para el documento email (el que hemos introducido) coger los campos
                    // Esta puesto asi porque en Firebase todos los datos de un usuario se corresponden a un email
                    dbFirebase.collection("usuario").document(email).get().addOnSuccessListener { usuario ->
                        val usr = usuario.data
                        if (usr != null) {
                            val emailUsr = usr["email"] as? String
                            val pwd = usr["password"] as? String
                            if (emailUsr != null && pwd != null) {
                                if (pwd == contrasena.text && usr["id"] != null) {
                                    FirebaseTemplate.iniciarSesion(email.trim(), contrasena.toString().trim())
                                    Toast.makeText(context,"Usuario inició sesión",Toast.LENGTH_SHORT).show()

                                } else {
                                    Toast.makeText(context,"Credenciales inválidas",Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context,"Datos del usuario incompletos",Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Usuario no existe", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("FIRESTORE_ERROR", "Error: ${exception.message}")
                        Toast.makeText(context, "Error: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                        //Toast.makeText(context, "Error en la consulta", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(text = "Iniciar sesion") // Texto del boton para iniciar sesion
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Fondo morado oscuro
                    contentColor = PurpleMedium  // Texto blanco
                ),
                onClick = {
                navController.navigate(route = AppScreens.Registro.route)
            }) {
                Text(text = "Registrarse")
            }
        }
    }
}