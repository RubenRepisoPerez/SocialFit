package com.example.socialfit.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.ArrowLeft
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comentarios(navController: NavController, idDoc: String, emailLocal: String) {
    val db = Firebase.firestore
    val context = LocalContext.current
    var nuevoComentario by remember { mutableStateOf("") }
    var comentarios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)

    // Escuchar comentarios en tiempo real
    LaunchedEffect(idDoc) {
        db.collection("publicaciones")
            .document(idDoc)
            .collection("comentarios")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firebase", "Error cargando comentarios", e)
                    return@addSnapshotListener
                }
                comentarios = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
            }
    }

    fun enviarComentario() {
        if (nuevoComentario.isBlank()) return

        val comentarioData = mapOf(
            "autorEmail" to emailLocal,
            "texto" to nuevoComentario,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("publicaciones")
            .document(idDoc)
            .collection("comentarios")
            .add(comentarioData)
            .addOnSuccessListener {
                nuevoComentario = ""
                // Opcional: Incrementar contador de comentarios en el post principal
                db.collection("publicaciones").document(idDoc)
                    .update("totalComentarios", FieldValue.increment(1))
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al comentar", Toast.LENGTH_SHORT).show()
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("COMENTARIOS", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PurpleDark)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = nuevoComentario,
                        onValueChange = { nuevoComentario = it },
                        placeholder = { Text("Añade un comentario...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = { enviarComentario() }) {
                        Icon(Lucide.Send, contentDescription = "Enviar", tint = PurpleDark)
                    }
                }
            }
        }
    ) { padding ->
        if (comentarios.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay comentarios aún. ¡Sé el primero!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(comentarios) { comentario ->
                    FilaComentario(comentario)
                }
            }
        }
    }
}

@Composable
fun FilaComentario(comentario: Map<String, Any>) {
    val db = Firebase.firestore
    val autorEmail = comentario["autorEmail"] as? String ?: ""
    val texto = comentario["texto"] as? String ?: ""
    var autorNick by remember { mutableStateOf("...") }
    var autorFoto by remember { mutableStateOf("") }

    LaunchedEffect(autorEmail) {
        db.collection("usuario").whereEqualTo("email", autorEmail).get().addOnSuccessListener {
            if (!it.isEmpty) {
                autorNick = it.documents[0].getString("nick") ?: "Usuario"
                autorFoto = it.documents[0].getString("fotoPerfil") ?: ""
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = autorFoto,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = autorNick, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                // Podrías añadir tiempo relativo aquí
            }
            Text(text = texto, fontSize = 14.sp, color = Color.Black)
        }
    }
}
