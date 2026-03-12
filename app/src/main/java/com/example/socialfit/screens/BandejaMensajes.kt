package com.example.socialfit.screens

import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun BandejaMensajes(navController: NavController, emailRecibido: String){

    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFDDE1E7) // Fondo de la pantalla
    val SurfaceWhite = Color(0xFFFFFFFF)       // Fondo de tarjetas o TextFields

    val storage = Firebase.storage
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var nick by remember { mutableStateOf("") }
    var idUsuario by remember { mutableStateOf("") }
    var TextoBuscado by remember { mutableStateOf("") }
    var chatsExistentes by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var usuariosSugeridos by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var emailsChatsFiltrados by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(emailRecibido) {
        dbFirebase.collection("chats")
            .whereArrayContains("participantes", emailRecibido)
            .orderBy("ultimoMomento", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chatsExistentes = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data?.toMutableMap()
                        data?.set("idDoc", doc.id)
                        val participantes = doc.get("participantes") as? List<String>
                        val otroEmail = participantes?.find { it != emailRecibido } ?: ""
                        data?.set("otroEmail", otroEmail)
                        data
                    }
                }
            }
    }

    LaunchedEffect(TextoBuscado, chatsExistentes) {
        if (TextoBuscado.isEmpty()) {
            usuariosSugeridos = emptyList()
            emailsChatsFiltrados = emptySet()
        } else {
            dbFirebase.collection("usuario").get().addOnSuccessListener { result ->
                val todosLosUsuarios = result.documents.mapNotNull { it.data }
                    .filter { it["email"] != emailRecibido }

                val texto = TextoBuscado.lowercase()
                val emailsConChat = chatsExistentes.mapNotNull { it["otroEmail"] as? String }.toSet()

                val usuariosQueCoinciden = todosLosUsuarios.filter { user ->
                    val nickU = (user["nick"] as? String ?: "").lowercase()
                    val nombreU = (user["nombre"] as? String ?: "").lowercase()
                    nickU.contains(texto) || nombreU.contains(texto)
                }

                emailsChatsFiltrados = usuariosQueCoinciden
                    .mapNotNull { it["email"] as? String }
                    .filter { emailsConChat.contains(it) }
                    .toSet()

                val filtradosNuevos = usuariosQueCoinciden.filter { user ->
                    val email = user["email"] as? String ?: ""
                    !emailsConChat.contains(email)
                }

                val empiezaNick = filtradosNuevos.filter { (it["nick"] as? String ?: "").lowercase().startsWith(texto) }
                val empiezaNombre = filtradosNuevos.filter { (it["nombre"] as? String ?: "").lowercase().startsWith(texto) && !empiezaNick.contains(it) }
                val contieneNick = filtradosNuevos.filter { (it["nick"] as? String ?: "").lowercase().contains(texto) && !empiezaNick.contains(it) && !empiezaNombre.contains(it) }
                val contieneNombre = filtradosNuevos.filter { (it["nombre"] as? String ?: "").lowercase().contains(texto) && !empiezaNick.contains(it) && !empiezaNombre.contains(it) && !contieneNick.contains(it) }

                usuariosSugeridos = empiezaNick + empiezaNombre + contieneNick + contieneNombre
            }
        }
    }

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
                        nick = document.getString("nick") ?: "Sin nick"
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
                        text = "Chats de $nick",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = TextoBuscado,
                onValueChange = { TextoBuscado = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Busca a tus seguidores para chatear...") },
                leadingIcon = { Icon(Lucide.Search,
                    contentDescription = null,
                    tint = PurpleDark) },
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

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val chatsAMostrar = if (TextoBuscado.isEmpty()) {
                    chatsExistentes
                } else {
                    chatsExistentes.filter { emailsChatsFiltrados.contains(it["otroEmail"]) }
                }

                items(chatsAMostrar) { chat ->
                    ItemChatBandeja(chat, navController, emailRecibido)
                }

                if (TextoBuscado.isNotEmpty() && usuariosSugeridos.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 1.dp,
                            color = PurpleMedium.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "Nuevos contactos",
                            fontSize = 12.sp,
                            color = PurpleDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(usuariosSugeridos) { usuario ->
                        ItemUsuarioNuevo(usuario, navController, emailRecibido)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemChatBandeja(chat: Map<String, Any>, navController: NavController, emailRecibido: String) {
    val PurpleDark = Color(0xFF2D1B4E)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)
    val db = Firebase.firestore

    var otroNick by remember { mutableStateOf(chat["otroNick"] as? String ?: "Cargando...") }
    var otroFoto by remember { mutableStateOf(chat["otroFoto"] as? String ?: "") }
    val otroEmail = chat["otroEmail"] as? String ?: ""
    var idUsuario by remember { mutableStateOf("") }


    LaunchedEffect(otroEmail) {
        if (otroEmail.isNotEmpty()) {
            val idEncontrado = FirebaseTemplate.obtenerIdConEmail(otroEmail)
            if (idEncontrado != null) {
                idUsuario = idEncontrado
                db.collection("usuario").document(idUsuario).get() // Usamos el email como ID si así lo tienes, o whereEqualTo
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            otroNick = document.getString("nick") ?: "Usuario"
                            otroFoto = document.getString("fotoPerfil") ?: ""
                        } else {
                            // Si el ID del documento no es el email, buscamos por campo
                            db.collection("usuario").whereEqualTo("email", otroEmail).get()
                                .addOnSuccessListener { snapshot ->
                                    if (!snapshot.isEmpty) {
                                        val doc = snapshot.documents[0]
                                        otroNick = doc.getString("nick") ?: "Usuario"
                                        otroFoto = doc.getString("fotoPerfil") ?: ""
                                    }
                                }
                        }
                    }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(route = AppScreens.Chat.route + "/" + emailRecibido + "/" + otroEmail)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(BackgroundGrayBlue),
                contentAlignment = Alignment.Center
            ) {
                if (otroFoto.isNotEmpty()) {
                    coil.compose.AsyncImage(
                        model = otroFoto,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Lucide.CircleUserRound,
                        contentDescription = null,
                        tint = PurpleDark,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    // USAMOS otroNick (el estado local) en lugar de chat["otroNick"]
                    text = otroNick,
                    fontWeight = FontWeight.Bold,
                    color = PurpleDark,
                    fontSize = 16.sp
                )
                Text(
                    text = chat["ultimoMensaje"] as? String ?: "Sin mensajes",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ItemUsuarioNuevo(usuario: Map<String, Any>, navController: NavController, emailRecibido: String) {
    ItemChatBandeja(
        chat = mapOf(
            "otroNick" to (usuario["nick"] as? String ?: ""),
            "otroEmail" to (usuario["email"] as? String ?: ""),
            "ultimoMensaje" to "Haz clic para iniciar una conversación",
            "otroFoto" to (usuario["fotoPerfil"] as? String ?: "")
        ),
        navController = navController,
        emailRecibido
    )
}