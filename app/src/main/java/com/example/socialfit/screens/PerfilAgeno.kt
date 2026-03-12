package com.example.socialfit.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.socialfit.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAgeno(navController: NavController, emailLocal: String, emailVisita: String) {

    val PurpleDark = Color(0xFF2D1B4E)
    val PurpleMedium = Color(0xFF6A4C93)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)

    val dbFirebase = Firebase.firestore

    var nick by remember { mutableStateOf("Cargando...") }
    var nombreU by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }
    var seguidores by remember { mutableIntStateOf(0) }
    var siguiendo by remember { mutableIntStateOf(0) }
    var rutinaU by remember { mutableStateOf("No hay rutina asignada") }
    val horariosSemana = remember { mutableStateMapOf<String, String>() }
    var altura by remember { mutableStateOf(0) }
    var peso by remember { mutableStateOf(0.0) }
    var sexo by remember { mutableStateOf("Desconocido") }
    var sexoBoolean by remember { mutableStateOf(true) }
    var yaLoSigue by remember { mutableStateOf(false) }

    var pesoTrapecio by remember { mutableStateOf(0.0) }
    var pesoHombro by remember { mutableStateOf(0.0) }
    var pesoRomboide by remember { mutableStateOf(0.0) }
    var pesoEspaldaBaja by remember { mutableStateOf(0.0) }
    var pesoTriceps by remember { mutableStateOf(0.0) }
    var pesoAntebrazo by remember { mutableStateOf(0.0) }
    var pesoGluteo by remember { mutableStateOf(0.0) }
    var pesoAbdomen by remember { mutableStateOf(0.0) }
    var pesoFemoral by remember { mutableStateOf(0.0) }
    var pesoCuadriceps by remember { mutableStateOf(0.0) }
    var pesoGemelo by remember { mutableStateOf(0.0) }
    var pesoPecho by remember { mutableStateOf(0.0) }
    var pesoBiceps by remember { mutableStateOf(0.0) }
    var pesoAbductores by remember { mutableStateOf(0.0) }
    val marcas = remember { mutableStateMapOf<String, Double>() }

    LaunchedEffect(emailLocal, emailVisita) {
        dbFirebase.collection("seguimientos")
            .whereEqualTo("seguidor", emailLocal)
            .whereEqualTo("seguido", emailVisita)
            .get()
            .addOnSuccessListener { documents ->
                yaLoSigue = !documents.isEmpty
            }
        dbFirebase.collection("seguimientos")
            .whereEqualTo("seguido", emailVisita)
            .get()
            .addOnSuccessListener { snapshot ->
                seguidores = snapshot.size()
            }

        dbFirebase.collection("seguimientos")
            .whereEqualTo("seguidor", emailVisita)
            .get()
            .addOnSuccessListener { snapshot ->
                siguiendo = snapshot.size()
            }
    }

    LaunchedEffect(emailVisita) {
        dbFirebase.collection("usuario")
            .whereEqualTo("email", emailVisita)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    nick = doc.getString("nick") ?: ""
                    nombreU = doc.getString("nombre") ?: ""
                    descripcion = doc.getString("descripcion") ?: ""
                    fotoUrl = doc.getString("fotoPerfil") ?: ""
                    rutinaU = doc.getString("rutina") ?: "Sin rutina asignada"
                    sexo = doc.getString("sexo") ?: "Sin sexo"
                    sexoBoolean = (sexo == "Hombre")
                    altura = doc.getLong("altura")?.toInt() ?: 0
                    peso = doc.getDouble("peso") ?: 0.0

                    val h = doc.get("horarios") as? Map<String, String>
                    h?.forEach { (k, v) -> horariosSemana[k] = v }

                    val marcasMap = doc.get("marcas") as? Map<String, Any>

                    fun obtenerPeso(nombreMusculo: String): Double {
                        val valor = marcasMap?.get(nombreMusculo)
                        return when (valor) {
                            is Number -> valor.toDouble()
                            else -> 0.0
                        }
                    }

                    pesoPecho = obtenerPeso("Pecho")
                    pesoBiceps = obtenerPeso("Bíceps")
                    pesoTriceps = obtenerPeso("Triceps")
                    pesoHombro = obtenerPeso("Deltoide")
                    pesoTrapecio = obtenerPeso("Trapecio")
                    pesoCuadriceps = obtenerPeso("Cuadriceps")
                    pesoEspaldaBaja = obtenerPeso("Espalda Baja")
                    pesoAbdomen = obtenerPeso("Abdomen")
                    pesoAntebrazo = obtenerPeso("Antebrazo")
                    pesoRomboide = obtenerPeso("Hombro Posterior")
                    pesoGluteo = obtenerPeso("Glúteo")
                    pesoFemoral = obtenerPeso("Bíceps Femoral")
                    pesoAbductores = obtenerPeso("Abductor")
                    pesoGemelo = obtenerPeso("Gemelo")


                    // Si también quieres llenar el mapa 'marcas' que tenías:
                    val nombresMusculos = listOf("Pecho", "Biceps", "Triceps", "Hombros", "Piernas", "Espalda", "Abdomen")
                    nombresMusculos.forEach { musculo ->
                        marcas[musculo] = obtenerPeso(musculo)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(nick, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PurpleDark)
            )
        },
        containerColor = BackgroundGrayBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = fotoUrl,
                            contentDescription = "Perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Lucide.CircleUserRound, contentDescription = null, modifier = Modifier.size(50.dp), tint = PurpleDark)
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(text = nombreU, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PurpleDark)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "$seguidores", fontWeight = FontWeight.Bold, color = PurpleDark)
                        Text(text = " Seguidores", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "$siguiendo", fontWeight = FontWeight.Bold, color = PurpleDark)
                        Text(text = " Siguiendo", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (descripcion.isEmpty()) "Este usuario aún no tiene descripción." else descripcion,
                color = PurpleDark,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (yaLoSigue) {
                            dbFirebase.collection("seguimientos")
                                .whereEqualTo("seguidor", emailLocal)
                                .whereEqualTo("seguido", emailVisita)
                                .get()
                                .addOnSuccessListener { result ->
                                    for (doc in result) {
                                        dbFirebase.collection("seguimientos").document(doc.id).delete()
                                    }
                                    yaLoSigue = false
                                }
                        } else {
                            val nuevoSeguimiento = hashMapOf(
                                "seguidor" to emailLocal,
                                "seguido" to emailVisita,
                                "fecha" to com.google.firebase.Timestamp.now()
                            )

                            dbFirebase.collection("seguimientos").add(nuevoSeguimiento)
                                .addOnSuccessListener {
                                    yaLoSigue = true
                                }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (yaLoSigue) Color.Gray else PurpleDark
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (yaLoSigue) Lucide.UserCheck else Lucide.UserPlus,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (yaLoSigue) "Siguiendo" else "Seguir")
                }

                Button(
                    onClick = { /* Lógica de mensajes */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, PurpleDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Lucide.MessageCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = PurpleDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mensaje", color = PurpleDark)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.Dumbbell, contentDescription = null, tint = PurpleDark, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rutina Actual", fontWeight = FontWeight.ExtraBold, color = PurpleDark, fontSize = 18.sp)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                IntrinsicSize.Min
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    // Barra lateral decorativa
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .background(
                                Brush.verticalGradient(listOf(AmberGold, PurpleMedium))
                            )
                    )

                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Lucide.ClipboardList, contentDescription = null, tint = PurpleMedium, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "PLAN SEMANAL",
                                color = PurpleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = rutinaU,
                            color = PurpleDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.CalendarDays, contentDescription = null, tint = PurpleDark, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Horarios de Entrenamiento", fontWeight = FontWeight.ExtraBold, color = PurpleDark, fontSize = 18.sp)
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val diasMap = listOf("L" to "Lunes", "M" to "Martes", "X" to "Miercoles", "J" to "Jueves", "V" to "Viernes", "S" to "Sabado", "D" to "Domingo")
                    diasMap.forEach { (letra, dia) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(30.dp).clip(RoundedCornerShape(6.dp)).background(PurpleDark),
                                    contentAlignment = Alignment.Center
                                ) { Text(letra, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(dia, color = PurpleDark, fontWeight = FontWeight.Medium)
                            }
                            Text(
                                text = horariosSemana[dia] ?: "No entrena",
                                color = if (horariosSemana.containsKey(dia)) AmberGold else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.BicepsFlexed, contentDescription = null, tint = PurpleDark, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Estadísticas de Fuerza", fontWeight = FontWeight.ExtraBold, color = PurpleDark, fontSize = 18.sp)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Margen externo para que no pegue a los bordes
                shape = RoundedCornerShape(16.dp), // Puntas redondeadas como en la imagen
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Estadisticas basadas en datos: $sexo, $peso Kg, $altura cm",
                        fontSize = 12.sp,
                        color = PurpleMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier.background(Color.Transparent),
                            contentAlignment = Alignment.TopCenter
                        ){
                            Image(
                                painter = painterResource(id = R.drawable.musculosfrontales),
                                contentDescription = "musculos frontales",
                            )
                            var colorHombro = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Deltoides", pesoHombro)
                            Image(
                                modifier = Modifier.padding(top = 47.dp),
                                painter = painterResource(id = R.drawable.hobrodelante),
                                contentDescription = "hombro",
                                colorFilter = ColorFilter.tint(colorHombro)
                            )
                            var colorPecho = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Pecho", pesoPecho)
                            Image(
                                modifier = Modifier.padding(top = 47.dp),
                                painter = painterResource(id = R.drawable.pecho),
                                contentDescription = "pecho",
                                colorFilter = ColorFilter.tint(colorPecho)
                            )
                            var colorBiceps = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Bíceps", pesoBiceps)
                            Image(
                                modifier = Modifier.padding(top = 62.dp),
                                painter = painterResource(id = R.drawable.biceps),
                                contentDescription = "biceps",
                                colorFilter = ColorFilter.tint(colorBiceps)
                            )
                            var colorAntebrazo = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Antebrazo", pesoAntebrazo)
                            Image(
                                modifier = Modifier.padding(top = 79.dp),
                                painter = painterResource(id = R.drawable.antebrazodelante),
                                contentDescription = "antebrazo",
                                colorFilter = ColorFilter.tint(colorAntebrazo)
                            )
                            var colorAbdomen = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Abdomen", pesoAbdomen)
                            Image(
                                modifier = Modifier.padding(top = 65.dp),
                                painter = painterResource(id = R.drawable.abdomendelante),
                                contentDescription = "abdomen",
                                colorFilter = ColorFilter.tint(colorAbdomen)
                            )
                            var colorAbductores = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Abductores", pesoAbductores)
                            Image(
                                modifier = Modifier.padding(top = 104.dp),
                                painter = painterResource(id = R.drawable.abductores),
                                contentDescription = "abductores",
                                colorFilter = ColorFilter.tint(colorAbductores)
                            )
                            var colorCuadriceps = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Cuádriceps", pesoCuadriceps)
                            Image(
                                modifier = Modifier.padding(top = 106.dp),
                                painter = painterResource(id = R.drawable.cuadricepsdelante),
                                contentDescription = "cuadriceps",
                                colorFilter = ColorFilter.tint(colorCuadriceps)
                            )
                            var colorGemelos = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Gemelo", pesoGemelo)
                            Image(
                                modifier = Modifier.padding(top = 161.dp),
                                painter = painterResource(id = R.drawable.gemelosdelante),
                                contentDescription = "gemelo",
                                colorFilter = ColorFilter.tint(colorGemelos)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(top = 35.dp)
                        ){
                            Image(
                                painterResource(id = R.drawable.barraavance),
                                contentDescription = null
                            )
                        }

                        Box(
                            modifier = Modifier.background(Color.Transparent),
                            contentAlignment = Alignment.TopCenter
                        ){
                            Image(
                                painter = painterResource(id = R.drawable.musculostraseros),
                                contentDescription = "musculos traseros",
                            )
                            var colorTrapecio = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Trapecio", pesoTrapecio)
                            Image(
                                modifier = Modifier.padding(top = 30.dp),
                                painter = painterResource(id = R.drawable.trapecio),
                                contentDescription = "trapecio",
                                colorFilter = ColorFilter.tint(colorTrapecio)
                            )
                            var colorHombro = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Deltoides", pesoHombro)
                            Image(
                                modifier = Modifier.padding(top = 41.dp),
                                painter = painterResource(id = R.drawable.hombrosdetras),
                                contentDescription = "hombroDetras",
                                colorFilter = ColorFilter.tint(colorHombro)
                            )
                            var colorRomboide = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Hombro Posterior", pesoRomboide)
                            Image(
                                modifier = Modifier.padding(top = 46.dp),
                                painter = painterResource(id = R.drawable.romboides),
                                contentDescription = "romboide",
                                colorFilter = ColorFilter.tint(colorRomboide)
                            )
                            var colorTriceps = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Tríceps", pesoTriceps)
                            Image(
                                modifier = Modifier.padding(top = 52.dp),
                                painter = painterResource(id = R.drawable.triceps),
                                contentDescription = "triceps",
                                colorFilter = ColorFilter.tint(colorTriceps)
                            )
                            var colorAntebrazo = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Antebrazo", pesoAntebrazo)
                            Image(
                                modifier = Modifier.padding(top = 71.dp),
                                painter = painterResource(id = R.drawable.antebrazodetras),
                                contentDescription = "triceps",
                                colorFilter = ColorFilter.tint(colorAntebrazo)
                            )
                            var colorEspaldaBaja = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Espalda Baja", pesoEspaldaBaja)
                            Image(
                                modifier = Modifier.padding(top = 56.dp),
                                painter = painterResource(id = R.drawable.espaldabaja),
                                contentDescription = "espaldaBaja",
                                colorFilter = ColorFilter.tint(colorEspaldaBaja)
                            )
                            var colorAbdomen = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Abdomen", pesoAbdomen)
                            Image(
                                modifier = Modifier.padding(top = 75.dp),
                                painter = painterResource(id = R.drawable.abdomendetras),
                                contentDescription = "abdomenDetras",
                                colorFilter = ColorFilter.tint(colorAbdomen)
                            )
                            var colorGluteo = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Glúteo", pesoGluteo)
                            Image(
                                modifier = Modifier.padding(top = 91.dp),
                                painter = painterResource(id = R.drawable.gluteo),
                                contentDescription = "gluteo",
                                colorFilter = ColorFilter.tint(colorGluteo)
                            )
                            var colorCuadriceps = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Cuádriceps", pesoCuadriceps)
                            Image(
                                modifier = Modifier.padding(top = 107.dp),
                                painter = painterResource(id = R.drawable.cuadricepsdetras),
                                contentDescription = "cuadricepsDetras",
                                colorFilter = ColorFilter.tint(colorCuadriceps)
                            )
                            var colorFemoral = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Bíceps Femoral", pesoFemoral)
                            Image(
                                modifier = Modifier.padding(top = 117.dp),
                                painter = painterResource(id = R.drawable.femoral),
                                contentDescription = "femoral",
                                colorFilter = ColorFilter.tint(colorFemoral)
                            )
                            var colorGemelos = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Gemelo", pesoGemelo)
                            Image(
                                modifier = Modifier.padding(top = 152.dp),
                                painter = painterResource(id = R.drawable.gemelos),
                                contentDescription = "gemelos",
                                colorFilter = ColorFilter.tint(colorGemelos)
                            )
                        }
                    }
                }
            }
        }
    }
}