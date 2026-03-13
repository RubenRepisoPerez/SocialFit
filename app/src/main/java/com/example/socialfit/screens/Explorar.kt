package com.example.socialfit.screens

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.example.socialfit.FirebaseTemplate
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun Explorar(navController: NavController, emailRecibido: String){

    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFDDE1E7) // Fondo de la pantalla
    val SurfaceWhite = Color(0xFFFFFFFF)

    // BBDD Firebase
    val storage = Firebase.storage
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var idUsuario by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val idEncontrado = FirebaseTemplate.obtenerIdConEmail(emailRecibido)
        if (idEncontrado != null) {
            idUsuario = idEncontrado
            val resultadoVerificacion = FirebaseTemplate.verificarYActualizarEstado(emailRecibido, idEncontrado)
            if (resultadoVerificacion.isSuccess) {
                val estaVerificado = resultadoVerificacion.getOrDefault(false)
                if (estaVerificado) {
                    Log.d("Firebase", "Usuario verificado detectado, actualizando UI")
                } else{
                    Log.d("Firebase", "Usuario NO verificado detectado")
                }
            } else {
                Log.e("Firebase", "Error al verificar: ${resultadoVerificacion.exceptionOrNull()?.message}")
            }

            dbFirebase.collection("usuario").document(idEncontrado).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {

                    }
                }
        }
    }

    Scaffold(
        containerColor = BackgroundGrayBlue,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    Text(
                        text = "Explora Contenido",
                        fontSize = 30.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },

                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White
                ),
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.height(90.dp),
                containerColor = PurpleDark,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        //navController.navigate(route = AppScreens.Explorar.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Dumbbell,
                            contentDescription = "Ir a explorar",
                            tint = if (true) PurpleDark else AmberGold
                        )
                    },
                    label = { Text("Explorar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleDark,
                        selectedTextColor = AmberGold,
                        indicatorColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(route = AppScreens.BandejaMensajes.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Text, contentDescription = "Ir a mensajes"
                        )
                    },
                    label = { Text("Mensajes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,
                        selectedTextColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(route = AppScreens.Buscar.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Search,
                            contentDescription = "Ir a buscar",
                        )
                    },
                    label = { Text("Buscar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,
                        selectedTextColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(route = AppScreens.Perfil.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.CircleUserRound,
                            contentDescription = "Perfil",
                        )
                    },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,
                        selectedTextColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
            }
        }) { innerPadding ->

    }
}