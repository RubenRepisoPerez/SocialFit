package com.example.socialfit.screens

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun BandejaMensajes(navController: NavController, emailRecibido: String){

    // Colores
    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFDDE1E7) // Fondo de la pantalla
    val SurfaceWhite = Color(0xFFFFFFFF)       // Fondo de tarjetas o TextFields

    // BBDD Firebase
    val storage = Firebase.storage
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        containerColor = BackgroundGrayBlue,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    Text(
                        text = "Busca Usuarios",
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
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.MisInmuebles.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Dumbbell, contentDescription = "Ir a explorar"
                        )
                    },
                    label = { Text("Explorar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,
                        selectedTextColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        //navController.navigate(route = AppScreens.Mensajes.route)
                    },
                    icon = {
                        Icon(
                            Lucide.Text,
                            contentDescription = "Ir a mensajes",
                            tint = if (true) PurpleDark else AmberGold
                        )
                    },
                    label = { Text("Mensajes") },
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