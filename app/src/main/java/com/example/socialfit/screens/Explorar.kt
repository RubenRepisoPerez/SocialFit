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
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*

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

    var tabSeleccionada by remember { mutableIntStateOf(0) } // 0 = Diario, 1 = Contenido
    var puedeSubirFoto by remember { mutableStateOf(false) }
    var fotosDiarias by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var postsComunidad by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var yaSubioFotoHoy by remember { mutableStateOf(false) }

    // Verificar si el usuario ya subió su foto hoy
    LaunchedEffect(emailRecibido) {
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Escuchamos el documento específico que se crea al subir la foto
        dbFirebase.collection("fotosDiarias")
            .document(hoy)
            .collection("fotos")
            .document(emailRecibido) // El documento tiene el nombre del email
            .addSnapshotListener { snapshot, _ ->
                // Si el documento existe, es que ya subió foto hoy
                yaSubioFotoHoy = snapshot != null && snapshot.exists()
            }
    }

    LaunchedEffect(idUsuario) {
        if (idUsuario.isNotEmpty()) {
            val localeEs = Locale("es", "ES")
            val calendario = Calendar.getInstance()
            val nombreDiaHoy = SimpleDateFormat("EEEE", localeEs).format(calendario.time)
                .replaceFirstChar { it.uppercase() }
            val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendario.time)

            dbFirebase.collection("usuario").document(idUsuario).get()
                .addOnSuccessListener { doc ->
                    val horarios = doc.get("horarios") as? Map<String, String>
                    val horarioHoy = horarios?.get(nombreDiaHoy)

                    if (horarioHoy != null && horarioHoy != "No entrena" && horarioHoy != "00:00 - 00:00") {
                        val partes = horarioHoy.split(" - ")
                        if (partes.size == 2) {
                            val inicio = partes[0].trim()
                            val fin = partes[1].trim()
                            // Solo permitimos subir si está en hora
                            puedeSubirFoto = horaActual >= inicio && horaActual <= fin
                        }
                    }
                }
        }
    }
    // Lógica para saber si el usuario está en su horario de entrenamiento
    LaunchedEffect(Unit) {
        // 1. Forzar Localización en Español para el nombre del día
        val localeEs = Locale("es", "ES")
        val calendario = Calendar.getInstance()

        // Obtenemos el día y lo ponemos en formato "Lunes", "Martes"...
        val nombreDiaHoy = SimpleDateFormat("EEEE", localeEs).format(calendario.time)
            .replaceFirstChar { it.uppercase() }

        // 2. Formato de hora actual en 24h (siempre con dos dígitos: 09:05)
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendario.time)

        val idEncontrado= FirebaseTemplate.obtenerIdConEmail(emailRecibido)

        if (idEncontrado != null){
            //Toast.makeText(context, "EL USUARIO SI SE HA ENCONTRADO", Toast.LENGTH_SHORT).show()
            dbFirebase.collection("usuario").document(idEncontrado).get()
                .addOnSuccessListener { doc ->
                    val horarios = doc.get("horarios") as? Map<String, String>
                    val horarioHoy = horarios?.get(nombreDiaHoy)
                    Toast.makeText(context, nombreDiaHoy.toString(), Toast.LENGTH_SHORT).show()

                    if (horarioHoy != null && horarioHoy != "No entrena" && horarioHoy != "00:00 - 00:00") {
                        try {
                            val partes = horarioHoy.split(" - ")
                            if (partes.size == 2) {
                                val inicio = partes[0].trim()
                                val fin = partes[1].trim()

                                // Comparación de strings: "18:00" >= "17:00" && "18:00" <= "19:00"
                                puedeSubirFoto = horaActual >= inicio && horaActual <= fin

                                Log.d("Horario", "Hoy: $nombreDiaHoy, Hora: $horaActual, Rango: $inicio a $fin. ¿Puede? $puedeSubirFoto")
                            }
                        } catch (e: Exception) {
                            Log.e("Horario", "Error al parsear el horario: ${e.message}")
                        }
                    }
                }
        }

    }

    // Cargar fotos diarias desde la subcolección del día de hoy
    LaunchedEffect(tabSeleccionada) {
        if (tabSeleccionada == 0) {
            val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Accedemos a fotosDiarias -> Documento de Hoy -> Subcolección fotos
            dbFirebase.collection("fotosDiarias")
                .document(hoy)
                .collection("fotos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snap, error ->
                    if (error != null) {
                        Log.e("Firebase", "Error cargando fotos: ${error.message}")
                        return@addSnapshotListener
                    }
                    fotosDiarias = snap?.documents?.mapNotNull { it.data } ?: emptyList()
                }
        }
    }

    LaunchedEffect(tabSeleccionada) {
        if (tabSeleccionada == 1) {
            dbFirebase.collection("publicaciones")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snap, _ ->
                    postsComunidad = snap?.documents?.mapNotNull { doc ->
                        val data = doc.data?.toMutableMap()
                        data?.set("idDoc", doc.id)
                        data
                    } ?: emptyList()
                }
        }
    }

    Scaffold(
        containerColor = BackgroundGrayBlue,
        topBar = {
            Column(modifier = Modifier.background(PurpleDark)) {
                CenterAlignedTopAppBar(
                    title = { Text("EXPLORAR", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PurpleDark)
                )
                // Selector de pestañas
                TabRow(
                    selectedTabIndex = tabSeleccionada,
                    containerColor = PurpleDark,
                    contentColor = AmberGold,
                    indicator = { TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(it[tabSeleccionada]), color = AmberGold) }
                ) {
                    Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 },
                        text = { Text("DIARIO", color = if(tabSeleccionada==0) AmberGold else Color.White) })
                    Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 },
                        text = { Text("CONTENIDO", color = if(tabSeleccionada==1) AmberGold else Color.White) })
                }
            }
        },
        floatingActionButton = {
            val mostrarBotonDiario = tabSeleccionada == 0 && puedeSubirFoto && !yaSubioFotoHoy
            val mostrarBotonContenido = tabSeleccionada == 1

            if (mostrarBotonDiario || mostrarBotonContenido) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val destino = if (tabSeleccionada == 0) "DIARIO" else "CONTENIDO"
                        navController.navigate("CamaraMensajes/${emailRecibido}/$destino")
                    },
                    containerColor = AmberGold,
                    contentColor = PurpleDark,
                    icon = { Icon(if(tabSeleccionada == 0) Lucide.Camera else Lucide.Plus, null) },
                    text = { Text(if(tabSeleccionada == 0) "FOTO DEL DÍA" else "PUBLICAR") }
                )
            }
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.height(90.dp),
                containerColor = PurpleDark,
                contentColor = Color.White,
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                NavigationBarItem(
                    modifier = Modifier.offset(y = (-12).dp),
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
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleDark,
                        selectedTextColor = AmberGold,
                        indicatorColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold
                    )
                )
                NavigationBarItem(
                    modifier = Modifier.offset(y = (-12).dp),
                    selected = false,
                    onClick = {
                        navController.navigate(route = AppScreens.BandejaMensajes.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Text, contentDescription = "Ir a mensajes"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,
                        selectedTextColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
                NavigationBarItem(
                    modifier = Modifier.offset(y = (-12).dp),
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
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,
                        selectedTextColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
                NavigationBarItem(
                    modifier = Modifier.offset(y = (-12).dp),
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
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    if (tabSeleccionada == 0) {
                        // SECCIÓN DIARIO
                        if (fotosDiarias.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Aún no hay fotos de entrenamiento hoy", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(fotosDiarias) { foto ->
                                    CardDiario(foto)
                                }
                            }
                        }
                    }
                    else if (tabSeleccionada == 1) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(postsComunidad) { post ->
                                PostCard(post, emailRecibido)
                            }
                        }
                    }
                    else {
                        // SECCIÓN CONTENIDO
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Contenido de la comunidad", color = PurpleDark)
                        }
                    }
                }
        }
}

@Composable
fun CardDiario(datos: Map<String, Any>) {
    val PurpleDark = Color(0xFF2D1B4E)
    val db = Firebase.firestore

    // Estados para la info del autor
    var autorNick by remember { mutableStateOf("Cargando...") }
    var autorFoto by remember { mutableStateOf("") }
    val autorEmail = datos["autorEmail"] as? String ?: ""

    LaunchedEffect(autorEmail) {
        if (autorEmail.isNotEmpty()) {
            db.collection("usuario").whereEqualTo("email", autorEmail).get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val doc = result.documents[0]
                        autorNick = doc.getString("nick") ?: "Usuario"
                        autorFoto = doc.getString("fotoPerfil") ?: ""
                    }
                }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de perfil circular
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    if (autorFoto.isNotEmpty()) {
                        AsyncImage(
                            model = autorFoto,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Lucide.CircleUserRound, null, tint = PurpleDark, modifier = Modifier.align(Alignment.Center))
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(autorNick, fontWeight = FontWeight.Bold, color = PurpleDark, fontSize = 15.sp)
                    Text("Entrenando hoy", color = Color.Gray, fontSize = 11.sp)
                }
            }

            // Imagen del entrenamiento
            AsyncImage(
                model = datos["fotoUrl"],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            // Comentario debajo de la foto
            if (!(datos["comentario"] as? String).isNullOrEmpty()) {
                Text(
                    text = datos["comentario"] as String,
                    modifier = Modifier.padding(16.dp),
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun PostCard(post: Map<String, Any>, emailLocal: String) {
    val db = Firebase.firestore
    val context = LocalContext.current
    var autorNick by remember { mutableStateOf("...") }
    var autorFoto by remember { mutableStateOf("") }
    val autorEmail = post["autorEmail"] as? String ?: ""
    val idDoc = post["idDoc"] as? String ?: ""

    // Cargar datos del autor
    LaunchedEffect(autorEmail) {
        db.collection("usuario").whereEqualTo("email", autorEmail).get().addOnSuccessListener {
            if (!it.isEmpty) {
                autorNick = it.documents[0].getString("nick") ?: ""
                autorFoto = it.documents[0].getString("fotoPerfil") ?: ""
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(bottom = 12.dp)) {

        // Cabeza de la publicacion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = autorFoto,
                contentDescription = null,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(10.dp))
            Text(text = autorNick, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(Modifier.weight(1f))
        }

        // Imagen o video
        val mediaUrl = post["mediaUrl"] as? String ?: ""
        val esVideo = post["tipo"] == "video"

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)) {

            if (esVideo && mediaUrl.isNotEmpty()) {
                // Reproductor del video
                val exoPlayer = remember {
                    androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                        setMediaItem(androidx.media3.common.MediaItem.fromUri(mediaUrl))
                        prepare()
                        playWhenReady = false // No empieza solo para no saturar
                        repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
                    }
                }

                // Si el video no esta dentro de la pantalla no se carga
                DisposableEffect(mediaUrl) {
                    onDispose { exoPlayer.release() }
                }

                AndroidView(
                    factory = { ctx ->
                        androidx.media3.ui.PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true // Permite pausar/reproducir
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Vista de la imagen
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Botones debajo de la publicacion
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)) {
            Icon(Lucide.Heart, null, modifier = Modifier.size(28.dp), tint = Color.Black)
            Spacer(Modifier.width(16.dp))
            Icon(Lucide.MessageCircle, null, modifier = Modifier.size(28.dp), tint = Color.Black)
            Spacer(Modifier.width(16.dp))
            Icon(Lucide.Send, null, modifier = Modifier.size(28.dp), tint = Color.Black)
            Spacer(Modifier.weight(1f))
            Icon(Lucide.Bookmark, null, modifier = Modifier.size(28.dp), tint = Color.Black)
        }

        // Comentario añadido al video
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            val comentario = post["comentario"] as? String ?: ""
            if (comentario.isNotEmpty()) {
                Row {
                    Text(text = autorNick, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(text = comentario, fontSize = 13.sp)
                }
            }
            Text(
                text = "Ver los comentarios...",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}