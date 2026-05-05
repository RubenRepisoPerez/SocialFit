package com.example.socialfit.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import com.example.socialfit.FirebaseTemplate
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.storage
import java.io.File
import java.util.Locale
import kotlin.io.path.exists

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(navController: NavController, emailLocal: String, emailVisita: String) {
    val db = Firebase.firestore
    val context = LocalContext.current
    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)

    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var nickVisita by remember { mutableStateOf("") }
    var nombreVisita by remember { mutableStateOf("") }
    var fotoVisita by remember { mutableStateOf("") }
    var idUsuario by remember { mutableStateOf("") }
    var mensajeTexto by remember { mutableStateOf("") }
    val mensajes = remember { mutableStateListOf<Map<String, Any>>() }
    val listState = rememberLazyListState()
    val chatId = if (emailLocal < emailVisita) "${emailLocal}_$emailVisita" else "${emailVisita}_$emailLocal"

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val encodedUri = Uri.encode(it.toString())
            navController.navigate("ImagenEnviar/$emailLocal/$emailVisita/$encodedUri")
        }
    }

    fun crearImagenUri(context: Context): Uri {
        val directory = File(context.externalCacheDir, "camera_photos")
        if (!directory.exists()) directory.mkdirs()
        val file = File.createTempFile("chat_pic_", ".jpg", directory)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            val encodedUri = Uri.encode(tempUri.toString())
            navController.navigate("ImagenEnviar/$emailLocal/$emailVisita/$encodedUri")
        }
    }

    LaunchedEffect(chatId) {
        val db = Firebase.firestore
        val actualizaciones = hashMapOf(
            "noLeidos.$emailLocal" to 0
        )
        db.collection("chats").document(chatId).set(actualizaciones, SetOptions.merge())
    }

    LaunchedEffect(emailVisita) {
        val idEncontrado = FirebaseTemplate.obtenerIdConEmail(emailVisita)
        if (idEncontrado != null){
            idUsuario = idEncontrado
            db.collection("usuario").document(idUsuario).get().addOnSuccessListener { doc ->
                nickVisita = doc.getString("nick") ?: ""
                nombreVisita = doc.getString("nombre") ?: ""
                fotoVisita = doc.getString("fotoPerfil") ?: ""
            }
        }
    }

    LaunchedEffect(emailLocal, emailVisita) {
        val chatId = if (emailLocal < emailVisita) "${emailLocal}_$emailVisita" else "${emailVisita}_$emailLocal"

        db.collection("chats").document(chatId).collection("mensajes")
            .orderBy("momento", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    mensajes.clear()
                    mensajes.addAll(snapshot.documents.mapNotNull { it.data })
                }
            }
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.size - 1)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)) {
                            if (fotoVisita.isNotEmpty()) {
                                AsyncImage(
                                    model = fotoVisita,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Lucide.CircleUserRound,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(nickVisita,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(nombreVisita,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Lucide.ArrowLeft,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(BackgroundGrayBlue)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(mensajes) { index, msg ->
                    val momentoActual = msg["momento"] as? Timestamp
                    val momentoPrevio = if (index > 0) mensajes[index - 1]["momento"] as? Timestamp else null

                    if (nuevoDia(momentoActual, momentoPrevio)) {
                        Text(
                            text = formatoFecha(momentoActual),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    val esMio = msg["emisor"] == emailLocal
                    val imagenUrl = msg["imagenUrl"] as? String
                    val videoUrl = msg["videoUrl"] as? String
                    val contenidoTexto = msg["contenido"] as? String

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (esMio) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = if (esMio) 16.dp else 0.dp,
                                bottomEnd = if (esMio) 0.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (esMio) PurpleDark else Color.White
                            )
                        ) {
                            Column {
                                if (!imagenUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = imagenUrl,
                                        contentDescription = "Imagen enviada",
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .sizeIn(maxWidth = 250.dp, maxHeight = 400.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                if (!videoUrl.isNullOrEmpty()) {
                                    VideoMessagePlayer(videoUrl = videoUrl)
                                }

                                if (!contenidoTexto.isNullOrEmpty()) {
                                    Text(
                                        text = contenidoTexto,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ),
                                        color = if (esMio) Color.White else PurpleDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
                color = BackgroundGrayBlue,
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = mensajeTexto,
                        onValueChange = { mensajeTexto = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe un mensaje...", fontSize = 14.sp) },
                        maxLines = 4,
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = {
                            IconButton(
                                modifier = Modifier
                                    .background(PurpleDark, CircleShape)
                                    .size(40.dp),
                                onClick = {
                                    navController.navigate(route = AppScreens.CamaraMensajes.route + "/" + emailLocal + "/" + emailVisita)
                                }
                            ) {
                                Icon(
                                    Lucide.Camera,
                                    contentDescription = "Abrir cámara",
                                    tint = AmberGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        trailingIcon = {
                            if (mensajeTexto.isNotBlank()) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .height(35.dp)
                                        .width(60.dp)
                                        .background(PurpleDark, CircleShape),
                                    onClick = {
                                        if (mensajeTexto.isNotBlank()){
                                            enviarMensaje(emailLocal, emailVisita, mensajeTexto)
                                            mensajeTexto = ""
                                        }
                                    },
                                ) {
                                    Icon(
                                        Lucide.Send,
                                        contentDescription = "Enviar",
                                        tint = AmberGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleDark,
                            unfocusedBorderColor = PurpleDark.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

fun enviarMensaje(emisor: String, receptor: String, contenido: String) {
    val db = Firebase.firestore
    val chatId = if (emisor < receptor) "${emisor}_$receptor" else "${receptor}_$emisor"
    val momento = Timestamp.now()

    val mensaje = hashMapOf(
        "emisor" to emisor,
        "contenido" to contenido,
        "momento" to momento
    )

    db.collection("chats").document(chatId).collection("mensajes").add(mensaje)

    val actualizaciones = hashMapOf(
        "ultimoMensaje" to contenido,
        "ultimoMomento" to momento,
        "participantes" to listOf(emisor, receptor),
        "noLeidos.$receptor" to FieldValue.increment(1)
    )

    db.collection("chats").document(chatId).set(actualizaciones, SetOptions.merge())
}

fun nuevoDia(aztual: Timestamp?, previo: Timestamp?): Boolean {
    if (aztual == null) return false
    if (previo == null) return true
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(aztual.toDate()) != sdf.format(previo.toDate())
}

fun formatoFecha(momento: Timestamp?): String {
    if (momento == null) return ""
    val sdf = SimpleDateFormat("d 'de' MMMM", Locale( "es", "ES"))
    return sdf.format(momento.toDate())
}

fun subirImagenYEnviar(uri: Uri, emisor: String, receptor: String) {
    val db = Firebase.firestore
    val storageRef = Firebase.storage.reference
    val chatId = if (emisor < receptor) "${emisor}_$receptor" else "${receptor}_$emisor"

    // Nombre único para la imagen
    val fileName = "chat_images/$chatId/${System.currentTimeMillis()}.jpg"
    val imageRef = storageRef.child(fileName)

    // Subir a Firebase Storage
    imageRef.putFile(uri).addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            val urlImagen = downloadUri.toString()

            // Enviar mensaje con la URL
            val momento = Timestamp.now()
            val mensaje = hashMapOf(
                "emisor" to emisor,
                "imagenUrl" to urlImagen,
                "momento" to momento,
                "tipo" to "imagen"
            )

            db.collection("chats").document(chatId).collection("mensajes").add(mensaje)

            // Actualizar bandeja de entrada
            db.collection("chats").document(chatId).set(
                mapOf(
                    "ultimoMensaje" to "Imagen",
                    "ultimoMomento" to momento,
                    "noLeidos.$receptor" to FieldValue.increment(1)
                ), SetOptions.merge()
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoMessagePlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            // No lo reproducimos automáticamente para no saturar al usuario
            playWhenReady = false
        }
    }

    // Liberar recursos cuando el mensaje sale de la pantalla
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(
        modifier = Modifier
            .sizeIn(maxWidth = 250.dp, maxHeight = 400.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                    // Ocultar controles automáticamente
                    hideController()
                    layoutParams = android.widget.FrameLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
