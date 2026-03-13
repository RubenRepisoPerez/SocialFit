package com.example.socialfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlin.text.contains
import kotlin.text.lowercase
import kotlin.text.startsWith

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun Buscar(navController: NavController, emailRecibido: String){

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

    var TextoBuscado by remember { mutableStateOf("") }
    var buscarPorNick by remember { mutableStateOf(true) } // true = Nick, false = Nombre
    var usuariosEncontrados by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(TextoBuscado, buscarPorNick) {
        if (TextoBuscado.isBlank()) {
            usuariosEncontrados = emptyList()
        } else {
            val campoFiltro = if (buscarPorNick) "nick" else "nombre"

            dbFirebase.collection("usuario")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.documents.mapNotNull { it.data }
                        .filter { it["email"] != emailRecibido }

                    val empiezan = lista.filter {
                        (it[campoFiltro] as? String)?.lowercase()?.startsWith(TextoBuscado.lowercase()) == true
                    }

                    val contienen = lista.filter {
                        val valor = (it[campoFiltro] as? String)?.lowercase() ?: ""
                        valor.contains(TextoBuscado.lowercase()) && !valor.startsWith(TextoBuscado.lowercase())
                    }

                    usuariosEncontrados = empiezan + contienen
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
                        navController.navigate(route = AppScreens.Explorar.route + "/" + emailRecibido)
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
                    selected = true,
                    onClick = {
                        //navController.navigate(route = AppScreens.Buscar.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Search,
                            contentDescription = "Ir a buscar",
                            tint = if (true) PurpleDark else AmberGold
                        )
                    },
                    label = { Text("Buscar") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = TextoBuscado,
                onValueChange = { TextoBuscado = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe para buscar...") },
                leadingIcon = { Icon(Lucide.Search, contentDescription = null, tint = PurpleDark) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PurpleDark,
                    unfocusedTextColor = PurpleDark,
                    focusedBorderColor = PurpleDark,
                    unfocusedBorderColor = PurpleMedium,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = AmberGold
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { buscarPorNick = true }) {
                    Text(
                        text = "Por Nick",
                        color = if (buscarPorNick) PurpleDark else Color.Gray,
                        fontWeight = if (buscarPorNick) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (buscarPorNick) TextDecoration.Underline else TextDecoration.None
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(onClick = { buscarPorNick = false }) {
                    Text(
                        text = "Por Nombre",
                        color = if (!buscarPorNick) PurpleDark else Color.Gray,
                        fontWeight = if (!buscarPorNick) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (!buscarPorNick) TextDecoration.Underline else TextDecoration.None
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usuariosEncontrados) { usuario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val emailUser = usuario["email"] as? String ?: ""
                                navController.navigate(AppScreens.PerfilAgeno.route + "/" + emailRecibido  + "/" + emailUser)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(55.dp) // Un poco más grande para que luzca mejor
                                    .clip(CircleShape) // Corta la imagen en forma de círculo
                                    .background(BackgroundGrayBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                val fotoUrl = usuario["fotoPerfil"] as? String

                                if (!fotoUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = fotoUrl,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop // Ajusta la imagen al círculo
                                    )
                                } else {
                                    Icon(
                                        Lucide.CircleUserRound,
                                        contentDescription = null,
                                        tint = PurpleDark,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = usuario["nick"] as? String ?: "Sin Nick",
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleDark,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = usuario["nombre"] as? String ?: "Sin Nombre",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}