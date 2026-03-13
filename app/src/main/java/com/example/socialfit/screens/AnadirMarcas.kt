package com.example.socialfit.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.socialfit.FirebaseTemplate
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.socialfit.R
import com.example.socialfit.navigation.AppScreens

data class EjercicioMarca(
    val nombre: String,
    val musculo: String,
    val imagenRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirMarcas(navController: NavController, emailRecibido: String) {
    val db = Firebase.firestore
    val context = LocalContext.current

    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)

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
        }
    }

    // Lista de ejercicios a mostrar
    val listaEjercicios = listOf(
        EjercicioMarca("Press Banca", "Pecho", R.drawable.ejerciciopecho),
        EjercicioMarca("Sentadilla", "Cuadriceps", R.drawable.ejerciciocuadriceps),
        EjercicioMarca("Peso Muerto Rumano", "Espalda Baja", R.drawable.ejercicioespaldabaja),
        EjercicioMarca("Elevaciones Laterales", "Deltoides", R.drawable.ejerciciodeltoides),
        EjercicioMarca("Curl Bíceps", "Bíceps", R.drawable.ejerciciobiceps),
        EjercicioMarca("Remo en T", "Trapecio", R.drawable.ejerciciotrapecio),
        EjercicioMarca("Pajaros", "Hombro Posterior", R.drawable.ejerciciohombroposterior),
        EjercicioMarca("Curl Femoral", "Bíceps Femoral", R.drawable.ejerciciofemoral),
        EjercicioMarca("Hip Thrust", "Glúteo", R.drawable.ejerciciogluteo),
        EjercicioMarca("Elevación Talones", "Gemelo", R.drawable.ejerciciogemelo),
        EjercicioMarca("Curl de Muñeca", "Antebrazo", R.drawable.ejercicioantebrazo),
        EjercicioMarca("Extensiones Polea", "Triceps", R.drawable.ejerciciotriceps),
        EjercicioMarca("Sentadilla Sumo", "Abductor", R.drawable.ejercicioaductores),
        EjercicioMarca("Crunch en Polea", "Abdomen", R.drawable.ejercicioabdomen),
        )

    Scaffold(
        containerColor = BackgroundGrayBlue,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Añadir Marcas", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(route = AppScreens.Perfil.route + "/" + emailRecibido) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PurpleDark
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listaEjercicios) { ejercicio ->
                CardEjercicio(
                    ejercicio = ejercicio,
                    onGuardar = { peso ->
                        // Guardamos en Firebase: usuario -> email -> marcas -> musculo
                        val data = mapOf(
                            "ejercicio" to ejercicio.nombre,
                            "peso" to peso,
                            "fecha" to System.currentTimeMillis()
                        )

                        db.collection("usuario").document(idUsuario)
                            .update("marcas.${ejercicio.musculo}", peso)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Marca de ${ejercicio.nombre} guardada", Toast.LENGTH_SHORT).show()
                            }
                    },
                    amberColor = AmberGold
                )
            }
        }
    }
}

@Composable
fun CardEjercicio(ejercicio: EjercicioMarca, onGuardar: (Double) -> Unit, amberColor: Color) {
    var pesoInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: Imagen y Nombre
            Column(
                modifier = Modifier.width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = ejercicio.imagenRes),
                    contentDescription = ejercicio.nombre,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = ejercicio.nombre,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Centro: Input de Peso
            OutlinedTextField(
                value = pesoInput,
                onValueChange = { pesoInput = it },
                label = { Text("Peso (kg)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = amberColor,
                    cursorColor = amberColor
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Derecha: Botón Confirmar
            Button(
                onClick = {
                    val peso = pesoInput.toDoubleOrNull()
                    if (peso != null) {
                        onGuardar(peso)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = amberColor),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}