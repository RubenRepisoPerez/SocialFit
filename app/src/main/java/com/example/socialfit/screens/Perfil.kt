package com.example.socialfit.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Cog
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pen
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.composables.icons.lucide.UserRound
import com.composables.icons.lucide.UserRoundPlus
import com.composables.icons.lucide.X
import com.example.socialfit.FirebaseTemplate
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun Perfil(navController: NavController, emailRecibido: String){

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
    LaunchedEffect(Unit) {
        // Quitamos "val" para usar la variable de arriba
        val idEncontrado = FirebaseTemplate.obtenerIdConEmail(emailRecibido)
        if (idEncontrado != null) {
            idUsuario = idEncontrado
        }
    }
    var descripcion by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        // Quitamos "val" para usar la variable de arriba
        val descripconEncontrada = FirebaseTemplate.obtenerIdConEmail(emailRecibido)
        if (descripconEncontrada != null) {
            descripcion = descripconEncontrada
        }
    }

    val datosUsuario = dbFirebase.collection("usuario").document(idUsuario).get()


    val context = LocalContext.current
    var descripcionNueva by remember { mutableStateOf("") }

    // Contar seguidores y siguiendo
    var seguidores = 0
    var siguiendo = 0

    // Editores
    var editarDescripcion by remember { mutableStateOf(false) }

    Scaffold(
        // Fondo transparente para ver la imagen
        containerColor = BackgroundGrayBlue,
        // Cabecera de la pantalla
        topBar = {
            CenterAlignedTopAppBar(modifier = Modifier.height(100.dp),
                title = {
                    Text(text = "Mi Perfil", fontSize = 30.sp, color = Color.White, fontWeight = FontWeight.Bold)
                },

                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White),

                actions = {
                    IconButton(onClick = {
                        //navController.popBackStack()
                        Toast.makeText(context, "Volver atrás", Toast.LENGTH_SHORT).show()
                    }
                    ) {
                        Icon(Lucide.Cog, "backIcon", tint = Color.White)
                    }

                }
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.height(90.dp),
                containerColor = PurpleDark,       // El fondo de la barra en morado oscuro
                contentColor = Color.White         // Color base para el contenido
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
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.Mensajes.route)
                    },
                    icon = {
                        Icon(
                            Lucide.Text, contentDescription = "Ir a mensajes"
                        )
                    },
                    label = { Text("Mensajes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.Productos.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Search, contentDescription = "Ir a buscar"
                        )
                    },
                    label = { Text("Buscar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.Resultados.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.CircleUserRound, contentDescription = "Ir al perfil de usuario"
                        )
                    },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
            }
        }) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.UserRound,
                        contentDescription = "Seguidores",
                        tint = PurpleMedium
                    )
                    Text(text = seguidores.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleMedium
                        )
                    Text("Seguidores", color = PurpleMedium)
                }

                Spacer(Modifier.width(150.dp))

                Column(Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.UserRoundPlus,
                        contentDescription = "Siguiendo",
                        tint = PurpleMedium
                    )
                    Text(text = siguiendo.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleMedium
                    )
                    Text("Siguiendo", color = PurpleMedium)
                }

            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Margen externo
                shape = RoundedCornerShape(20.dp), // Puntas redondeadas
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                // Usamos Row para poner la foto a la izquierda y el texto a la derecha
                Row(
                    modifier = Modifier
                        .padding(16.dp) // Espaciado interno
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Espacio para la foto de perfil
                    Surface(
                        modifier = Modifier.size(80.dp), // Tamaño del círculo
                        shape = CircleShape,
                        border = BorderStroke(2.dp, AmberGold), // El anillo dorado
                        color = Color.LightGray // Fondo mientras no hay foto
                    ) {
                        // Aquí iría tu Image(...)
                        // Image(painter = ..., contentDescription = "Foto de perfil", contentScale = ContentScale.Crop)
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Espacio entre foto y texto

                    // Datos del Usuario
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "FitQueen_Alejandra",
                                color = PurpleDark, // Color temático,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = {editarDescripcion = true},
                                modifier = Modifier.weight(1f).size(20.dp)) {
                                Icon(
                                    imageVector = Lucide.Pen,
                                    contentDescription = "Editar descripcion"
                                )
                            }
                        }

                        Text(
                            text = "Alejandra García",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = descripcion,
                            color = PurpleDark,
                        )
                    }

                    // Dialog para editar la descripcion
                    if (editarDescripcion){
                        Dialog(onDismissRequest = { editarDescripcion = false }) {
                            Box(
                                modifier = Modifier.width(340.dp)
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BackgroundGrayBlue)
                                    .padding(bottom = 24.dp)
                            ) {
                                IconButton(onClick = { editarDescripcion = false }){
                                    Icon(
                                        imageVector = Lucide.X,
                                        contentDescription = "Cerrar",
                                        modifier = Modifier.size(30.dp),
                                    )
                                }
                                // Pedimos el titulo
                                OutlinedTextField(
                                    value = descripcionNueva,
                                    onValueChange = {
                                        descripcionNueva = it
                                    },
                                    label = { Text("Descripcion") },
                                    modifier = Modifier.width(300.dp)
                                )
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PurpleDark, // Fondo morado oscuro
                                        contentColor = Color.White    // Texto blanco
                                    ),
                                    onClick = {
                                        // Actualizamos usando ese ID
                                        dbFirebase.collection("usuario")
                                            .document(idUsuario)
                                            .update("descripcion", descripcionNueva)
                                        Toast.makeText(context, "Descripción actualizada", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Actualizar descripcion")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}