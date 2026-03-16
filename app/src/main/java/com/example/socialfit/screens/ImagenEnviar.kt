package com.example.socialfit.screens

import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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
    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)

    var comentario by remember { mutableStateOf("") }
    var subiendo by remember { mutableStateOf(false) }
    val decodedUri = Uri.parse(Uri.decode(imageUri))

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
                AsyncImage(
                    model = decodedUri,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
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
                                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                focusedBorderColor = AmberGold,
                                unfocusedBorderColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        if (subiendo) {
                            CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(30.dp))
                        } else {
                            IconButton(
                                onClick = {
                                    subiendo = true
                                    ejecutarSubida(decodedUri, emailLocal, emailVisita, comentario) {
                                        navController.navigate(route = AppScreens.Chat.route + "/" + emailLocal + "/" + emailVisita)
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(AmberGold, CircleShape)
                            ) {
                                Icon(
                                    Lucide.Send,
                                    contentDescription = "Enviar",
                                    tint = PurpleDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ejecutarSubida(uri: Uri, emisor: String, receptor: String, texto: String, onComplete: () -> Unit) {
    val db = Firebase.firestore
    val storageRef = Firebase.storage.reference
    val chatId = if (emisor < receptor) "${emisor}_$receptor" else "${receptor}_$emisor"
    val fileName = "chat_images/$chatId/${System.currentTimeMillis()}.jpg"
    val imageRef = storageRef.child(fileName)

    imageRef.putFile(uri).addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            val momento = Timestamp.now()
            val mensaje = hashMapOf(
                "emisor" to emisor,
                "imagenUrl" to downloadUri.toString(),
                "contenido" to texto,
                "momento" to momento
            )

            db.collection("chats").document(chatId).collection("mensajes").add(mensaje)

            val actualizaciones = hashMapOf(
                "ultimoMensaje" to if (texto.isNotEmpty()) "Imagen y $texto" else "Imagen",
                "ultimoMomento" to momento,
                "noLeidos.$receptor" to FieldValue.increment(1)
            )
            db.collection("chats").document(chatId).set(actualizaciones,
                SetOptions.merge())
            onComplete()
        }
    }
}