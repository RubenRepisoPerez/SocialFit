package com.example.socialfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import com.example.socialfit.FirebaseTemplate
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import com.google.firebase.Timestamp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(navController: NavController, emailLocal: String, emailVisita: String) {
    val db = Firebase.firestore
    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)

    var nickVisita by remember { mutableStateOf("") }
    var nombreVisita by remember { mutableStateOf("") }
    var fotoVisita by remember { mutableStateOf("") }
    var idUsuario by remember { mutableStateOf("") }
    var mensajeTexto by remember { mutableStateOf("") }
    val mensajes = remember { mutableStateListOf<Map<String, Any>>() }
    val listState = rememberLazyListState()

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
                                Icon(Lucide.CircleUserRound,
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
                        Icon(Lucide.ArrowLeft,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    
                    if (isNewDay(momentoActual, momentoPrevio)) {
                        Text(
                            text = formatDate(momentoActual),
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
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (esMio) Alignment.CenterEnd else Alignment.CenterStart) {
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
                            Text(
                                text = msg["contenido"] as? String ?: "",
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BackgroundGrayBlue
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
                            IconButton(onClick = { /* Pendiente: Galería/Cámara */ }) {
                                Icon(
                                    Lucide.Image,
                                    contentDescription = "Enviar imagen",
                                    tint = PurpleDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        trailingIcon = {
                            if (mensajeTexto.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        enviarMensaje(emailLocal, emailVisita, mensajeTexto)
                                        mensajeTexto = ""
                                    },
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(32.dp)
                                        .background(PurpleDark, CircleShape)
                                ) {
                                    Icon(
                                        Lucide.Send,
                                        contentDescription = "Enviar",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleDark,
                            unfocusedBorderColor = PurpleDark.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
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

    db.collection("chats").document(chatId).set(
        mapOf(
            "ultimoMensaje" to contenido,
            "ultimoMomento" to momento,
            "participantes" to listOf(emisor, receptor)
        )
    )
}

fun isNewDay(current: Timestamp?, prev: Timestamp?): Boolean {
    if (current == null) return false
    if (prev == null) return true
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(current.toDate()) != sdf.format(prev.toDate())
}

fun formatDate(momento: Timestamp?): String {
    if (momento == null) return ""
    val sdf = SimpleDateFormat("d 'de' MMMM", Locale( "es", "ES"))
    return sdf.format(momento.toDate())
}