package com.example.socialfit.screens

import android.net.Uri
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.X
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagenEnviar(navController: NavController, emailLocal: String, emailVisita: String, imageUri: String) {
    val context = LocalContext.current
    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)

    var comentario by remember { mutableStateOf("") }
    var subiendo by remember { mutableStateOf(false) }
    val decodedUri = Uri.parse(Uri.decode(imageUri))

    val esVideo = remember(decodedUri) {
        val type = context.contentResolver.getType(decodedUri)
        type?.startsWith("video") == true || decodedUri.toString().contains(".mp4")
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(decodedUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text("Enviar imagen",
                    color = Color.White)
                        },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Lucide.X,
                            contentDescription = "Cancelar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    if (esVideo) {
                        // REPRODUCTOR DE VÍDEO
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = exoPlayer
                                    useController = true
                                    // Esto asegura que ocupe el espacio correcto
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // VISOR DE IMAGEN
                        AsyncImage(
                            model = decodedUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = comentario,
                            onValueChange = { comentario = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Añade un comentario...", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AmberGold,
                                unfocusedBorderColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        if (subiendo) {
                            CircularProgressIndicator(
                                color = AmberGold,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    subiendo = true
                                    ejecutarSubida(decodedUri, emailLocal, emailVisita, comentario, esVideo) {
                                        navController.navigate(AppScreens.Chat.route + "/$emailLocal/$emailVisita")
                                    }
                                },
                                modifier = Modifier.size(48.dp).background(AmberGold, CircleShape)
                            ) {
                                Icon(Lucide.Send, contentDescription = "Enviar", tint = PurpleDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ejecutarSubida(uri: Uri, emisor: String, receptor: String, texto: String, esVideo: Boolean, onComplete: () -> Unit) {
    val db = Firebase.firestore
    val storageRef = Firebase.storage.reference
    val chatId = if (emisor < receptor) "${emisor}_$receptor" else "${receptor}_$emisor"

    // Cambiar extensión según el tipo
    val extension = if (esVideo) "mp4" else "jpg"
    val fileName = "chat_media/$chatId/${System.currentTimeMillis()}.$extension"
    val fileRef = storageRef.child(fileName)

    fileRef.putFile(uri).addOnSuccessListener {
        fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
            val momento = Timestamp.now()

            // Creamos el mensaje. Si es video, usamos 'videoUrl', si no 'imagenUrl'
            val mensaje = hashMapOf(
                "emisor" to emisor,
                "contenido" to texto,
                "momento" to momento,
                if (esVideo) "videoUrl" to downloadUri.toString() else "imagenUrl" to downloadUri.toString()
            )

            db.collection("chats").document(chatId).collection("mensajes").add(mensaje)

            // Actualizar último mensaje en la bandeja
            val prefijo = if (esVideo) "🎥 Vídeo" else "📷 Imagen"
            val textoFinal = if (texto.isNotEmpty()) "$prefijo: $texto" else prefijo

            val actualizaciones = hashMapOf(
                "ultimoMensaje" to textoFinal,
                "ultimoMomento" to momento,
                "noLeidos.$receptor" to FieldValue.increment(1)
            )
            db.collection("chats").document(chatId).set(actualizaciones, SetOptions.merge())
            onComplete()
        }
    }
}