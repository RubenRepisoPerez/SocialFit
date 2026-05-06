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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.socialfit.R
import com.example.socialfit.navigation.AppScreens


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
                        modifier = Modifier.size(18.dp),
                        tint = if (yaLoSigue) PurpleDark else Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (yaLoSigue) "Siguiendo" else "Seguir", color = if (yaLoSigue) PurpleDark else Color.White)
                }

                Button(
                    onClick = {
                        navController.navigate(route = AppScreens.Chat.route + "/" + emailLocal + "/" + emailVisita)
                    },
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
                    val diasMap = listOf("L" to "Lunes", "M" to "Martes", "X" to "Miércoles", "J" to "Jueves", "V" to "Viernes", "S" to "Sábado", "D" to "Domingo")
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
                        ConstraintLayout(
                            modifier = Modifier
                                .background(Color.Transparent),
                        ) {
                            val (fondo, hombro, pecho, biceps, antebrazo, abdomen, abductores, cuadriceps, gemelo) = createRefs()


                            val gHombroPecho = createGuidelineFromTop(0.21f)
                            val gBiceps = createGuidelineFromTop(0.27f)
                            val gAbdomen = createGuidelineFromTop(0.29f)
                            val gAntebrazo = createGuidelineFromTop(0.35f)
                            val gAbductores = createGuidelineFromTop(0.46f)
                            val gCuadriceps = createGuidelineFromTop(0.46f)
                            val gGemelos = createGuidelineFromTop(0.72f)

                            Image(
                                painter = painterResource(id = R.drawable.musculosfrontales),
                                contentDescription = "fondo",
                                modifier = Modifier.constrainAs(fondo) {
                                    top.linkTo(parent.top)
                                    centerHorizontallyTo(parent)
                                }
                            )

                            Image(
                                painter = painterResource(id = R.drawable.hobrodelante),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Deltoides", pesoHombro)),
                                modifier = Modifier.constrainAs(hombro) {
                                    top.linkTo(gHombroPecho) // Se pega a la línea del 12%
                                    centerHorizontallyTo(parent) // Siempre centrado
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.pecho),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Pecho", pesoPecho)),
                                modifier = Modifier.constrainAs(pecho) {
                                    top.linkTo(gHombroPecho)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.biceps),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Bíceps", pesoBiceps)),
                                modifier = Modifier.constrainAs(biceps) {
                                    top.linkTo(gBiceps)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.antebrazodelante),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Antebrazo", pesoAntebrazo)),
                                modifier = Modifier.constrainAs(antebrazo) {
                                    top.linkTo(gAntebrazo)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.abdomendelante),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Abdomen", pesoAbdomen)),
                                modifier = Modifier.constrainAs(abdomen) {
                                    top.linkTo(gAbdomen)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.abductores),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Abductores", pesoAbductores)),
                                modifier = Modifier.constrainAs(abductores) {
                                    top.linkTo(gAbductores)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.cuadricepsdelante),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Cuádriceps", pesoCuadriceps)),
                                modifier = Modifier.constrainAs(cuadriceps) {
                                    top.linkTo(gCuadriceps)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.gemelosdelante),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Gemelo", pesoGemelo)),
                                modifier = Modifier.constrainAs(gemelo) {
                                    top.linkTo(gGemelos)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
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

                        ConstraintLayout(
                            modifier = Modifier
                                .background(Color.Transparent),
                        ) {
                            val (fondo, trapecio, hombro, romboide, triceps, antebrazo, espaldaBaja, abdomen, gluteo, cuadriceps, femoral, gemelo) = createRefs()

                            val gTrapecio = createGuidelineFromTop(0.14f)    // 30dp
                            val gHombro = createGuidelineFromTop(0.19f)      // 41dp
                            val gRomboide = createGuidelineFromTop(0.21f)    // 46dp
                            val gTriceps = createGuidelineFromTop(0.24f)     // 52dp
                            val gEspaldaBaja = createGuidelineFromTop(0.26f) // 56dp
                            val gAntebrazo = createGuidelineFromTop(0.33f)   // 71dp
                            val gAbdomen = createGuidelineFromTop(0.35f)     // 75dp
                            val gGluteo = createGuidelineFromTop(0.42f)      // 91dp
                            val gCuadriceps = createGuidelineFromTop(0.49f)  // 107dp
                            val gFemoral = createGuidelineFromTop(0.53f)     // 117dp
                            val gGemelos = createGuidelineFromTop(0.69f)     // 152dp

                            // 3. Imagen de fondo (TRASERA) centrada
                            Image(
                                painter = painterResource(id = R.drawable.musculostraseros),
                                contentDescription = "musculos traseros",
                                modifier = Modifier.constrainAs(fondo) {
                                    top.linkTo(parent.top)
                                    centerHorizontallyTo(parent)
                                }
                            )

                            // 4. Músculos (Centrados y anclados a sus nuevas guías)
                            Image(
                                painter = painterResource(id = R.drawable.trapecio),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Trapecio", pesoTrapecio)),
                                modifier = Modifier.constrainAs(trapecio) {
                                    top.linkTo(gTrapecio)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.hombrosdetras),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Deltoides", pesoHombro)),
                                modifier = Modifier.constrainAs(hombro) {
                                    top.linkTo(gHombro)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.romboides),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Hombro Posterior", pesoRomboide)),
                                modifier = Modifier.constrainAs(romboide) {
                                    top.linkTo(gRomboide)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.triceps),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Tríceps", pesoTriceps)),
                                modifier = Modifier.constrainAs(triceps) {
                                    top.linkTo(gTriceps)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.espaldabaja),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Espalda Baja", pesoEspaldaBaja)),
                                modifier = Modifier.constrainAs(espaldaBaja) {
                                    top.linkTo(gEspaldaBaja)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.antebrazodetras),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Antebrazo", pesoAntebrazo)),
                                modifier = Modifier.constrainAs(antebrazo) {
                                    top.linkTo(gAntebrazo)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.abdomendetras),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Abdomen", pesoAbdomen)),
                                modifier = Modifier.constrainAs(abdomen) {
                                    top.linkTo(gAbdomen)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.gluteo),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Glúteo", pesoGluteo)),
                                modifier = Modifier.constrainAs(gluteo) {
                                    top.linkTo(gGluteo)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.cuadricepsdetras),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Cuádriceps", pesoCuadriceps)),
                                modifier = Modifier.constrainAs(cuadriceps) {
                                    top.linkTo(gCuadriceps)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.femoral),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Bíceps Femoral", pesoFemoral)),
                                modifier = Modifier.constrainAs(femoral) {
                                    top.linkTo(gFemoral)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )

                            Image(
                                painter = painterResource(id = R.drawable.gemelos),
                                colorFilter = ColorFilter.tint(getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Gemelo", pesoGemelo)),
                                modifier = Modifier.constrainAs(gemelo) {
                                    top.linkTo(gGemelos)
                                    centerHorizontallyTo(parent)
                                },
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}