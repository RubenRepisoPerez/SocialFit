package com.example.socialfit.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Cog
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.OptIn

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun Ajustes(navController: NavController, emailRecibido: String){

    // Colores
    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFDDE1E7) // Fondo de la pantalla
    val SurfaceWhite = Color(0xFFFFFFFF)       // Fondo de tarjetas o TextFields

    // BBDD Firebase
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()

    var idUsuario by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var nick by remember { mutableStateOf("") }
    var nombreU by remember { mutableStateOf("") }
    var seguidores by remember { mutableStateOf(0) }
    var siguiendo by remember { mutableStateOf(0)}
    var rutinaU by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val context = LocalContext.current

    Scaffold(
        // Fondo transparente para ver la imagen
        containerColor = BackgroundGrayBlue,
        // Cabecera de la pantalla
        topBar = {
            CenterAlignedTopAppBar(modifier = Modifier.height(100.dp),
                title = {
                    Text(text = "Ajustes", fontSize = 30.sp, color = Color.White, fontWeight = FontWeight.Bold)
                },

                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White),

                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(route = AppScreens.Perfil.route + "/" + emailRecibido)
                        Toast.makeText(context, "Volver a perfil", Toast.LENGTH_SHORT).show()
                    }
                    ) {
                        Icon(Lucide.ArrowLeft, "backIcon", tint = Color.White)
                    }
                }
            )
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(
                onClick = {
                    navController.navigate(route = AppScreens.InicioSesion.route)
                }
            ) {
                Text(text = "Cerrar Sesión")
            }
        }
    }
}