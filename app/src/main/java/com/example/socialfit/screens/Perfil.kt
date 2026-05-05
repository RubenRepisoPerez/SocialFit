package com.example.socialfit.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Cog
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pen
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.composables.icons.lucide.User
import com.composables.icons.lucide.UserRound
import com.composables.icons.lucide.UserRoundPlus
import com.composables.icons.lucide.X
import com.example.socialfit.FirebaseTemplate
import com.example.socialfit.R
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import androidx.constraintlayout.compose.Dimension

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun Perfil(navController: NavController, emailRecibido: String){

    // Colores
    val PurpleDark = Color(0xFF2D1B4E)      // Primario (Barras, Botón Principal)
    val PurpleMedium = Color(0xFF4A3175)    // Secundario (Bordes de inputs, Botones secundarios)
    val AmberGold = Color(0xFFFFC107)       // (Iconos, Checkbox, RadioButtons, Errores)
    val BackgroundGrayBlue = Color(0xFFDDE1E7) // Fondo de la pantalla
    val SurfaceWhite = Color(0xFFFFFFFF)       // Fondo de tarjetas o TextFields

    // BBDD Firebase
    val storage = Firebase.storage
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var idUsuario by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var nick by remember { mutableStateOf("") }
    var nombreU by remember { mutableStateOf("") }
    var seguidores by remember { mutableIntStateOf(0) }
    var siguiendo by remember { mutableIntStateOf(0) }
    var rutinaU by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var altura by remember { mutableStateOf(0) }
    var peso by remember { mutableStateOf(0.0) } // Cambiado a Double
    var sexo by remember { mutableStateOf("Desconocido") } // Nuevo estado para sexo
    var sexoBoolean by remember { mutableStateOf(true) }

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

    val horariosSemana = remember { mutableStateMapOf<String, String>() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var loadingImage by remember { mutableStateOf(false) }
    val marcas = remember { mutableStateMapOf<String, Double>() }

    // Cargar seguidores y siguiendo al iniciar
    LaunchedEffect(emailRecibido) {
        if (emailRecibido.isNotEmpty()) {
            val listenerSeguidores = dbFirebase.collection("seguimientos")
                .whereEqualTo("seguido", emailRecibido)
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null) {
                        seguidores = snapshot.size()
                    }
                }

            val listenerSiguiendo = dbFirebase.collection("seguimientos")
                .whereEqualTo("seguidor", emailRecibido)
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null) {
                        siguiendo = snapshot.size()
                    }
                }
        }
    }

    // Cargar imagen de perfil y galeria para seleccionar imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            loadingImage = true
            // Ruta en Storage, carpeta 'fotos_perfil' con el nombre del ID del usuario
            val storageRef = storage.reference.child("fotos_perfil/$idUsuario.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // Guardamos el LINK de la imagen en el documento del usuario en Firestore
                        dbFirebase.collection("usuario").document(idUsuario)
                            .update("fotoPerfil", downloadUrl.toString())
                            .addOnSuccessListener {
                                imageUri = downloadUrl // Actualizamos la vista
                                loadingImage = false
                            }
                    }
                }
                .addOnFailureListener {
                    loadingImage = false
                    Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Estados para el TimePicker
    var verHoraInicio by remember { mutableStateOf(false) }
    var verHoraFinalizacion by remember { mutableStateOf(false) }
    var diaSeleccionado by remember { mutableStateOf("") }

    var tiempoInicio = rememberTimePickerState(is24Hour = true)
    var tiempoFinalizacion = rememberTimePickerState(is24Hour = true)

    // Cargar horarios desde Firebase al iniciar
    LaunchedEffect(idUsuario) {
        if (idUsuario.isNotEmpty()) {
            dbFirebase.collection("usuario").document(idUsuario)
                .get()
                .addOnSuccessListener { document ->
                    val horariosSubido = document.get("horarios") as? Map<String, String>
                    horariosSubido?.forEach { (dia, hora) ->
                        horariosSemana[dia] = hora
                    }
                }
        }
    }

    // Un mapa o estado para guardar los horarios de cada día
    val horariosDias = remember { mutableStateMapOf<String, String>().apply {
        listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo").forEach {
            put(it, "17:00 - 18:30") // Valor por defecto
        }
    }}


    // Lista rutinas
    var listaRutinas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Carga datos varios del usuario con su email
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

            // Buscamos los datos del perfil
            dbFirebase.collection("usuario").document(idEncontrado).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        descripcion = document.getString("descripcion") ?: "Sin descripción"
                        nick = document.getString("nick") ?: "Sin nick"
                        nombreU = document.getString("nombre") ?: "Sin nombre"
                        rutinaU = document.getString("rutina") ?: "Sin rutina"
                        sexo = document.getString("sexo") ?: "Sin sexo"

                        sexoBoolean = (sexo == "Hombre")

                        altura = document.getLong("altura")?.toInt() ?: 0
                        peso = document.getDouble("peso") ?: 0.0

                        val fotoUrl = document.getString("fotoPerfil")
                        if (!fotoUrl.isNullOrEmpty()) {
                            imageUri = Uri.parse(fotoUrl)
                        }

                        val marcasMap = document.get("marcas") as? Map<String, Any>

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
                        pesoHombro = obtenerPeso("Deltoides")
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


                        // LLenamos las marcas de pesos tambien
                        val nombresMusculos = listOf("Pecho", "Biceps", "Triceps", "Hombros", "Piernas", "Espalda", "Abdomen")
                        nombresMusculos.forEach { musculo ->
                            marcas[musculo] = obtenerPeso(musculo)
                        }
                    }
                }
        } else {
            descripcion = "Usuario no encontrado"
        }
    }


    var descripcionNueva by remember { mutableStateOf("") }


    // Editores
    var editarDescripcion by remember { mutableStateOf(false) }
    var editarRutina by remember { mutableStateOf(false) }
    var editarDatosFisicos by remember { mutableStateOf(false) }

    // Cada vez que se intente abrir el diálogo, refrescamos la lista de rutinas
    LaunchedEffect(editarRutina) {
        if (editarRutina) {
            dbFirebase.collection("rutina").get()
                .addOnSuccessListener { result ->
                    // Guardamos el nombre de la rutina y el campo de días
                    listaRutinas = result.map { doc ->
                        mapOf(
                            "nombre" to doc.id,         // El ID es el nombre de la rutina
                            "Dias" to (doc.get("Dias") ?: 0) // El campo con la duración
                        )
                    }
                }
        }
    }

    Scaffold(
        // Fondo transparente para ver la imagen
        containerColor = BackgroundGrayBlue,
        // Cabecera de la pantalla
        topBar = {
            CenterAlignedTopAppBar(modifier = Modifier.height(100.dp),
                title = {
                    Text(text = "Mi Perfil",
                        fontSize = 30.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },

                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White),

                // Pantalla de ajustes, cerrar sesion de momento
                actions = {
                    IconButton(onClick = {
                        navController.navigate(route = AppScreens.Ajustes.route + "/" + emailRecibido)
                        Toast.makeText(context, "Ajustes", Toast.LENGTH_SHORT).show()
                    }
                    ) {
                        Icon(Lucide.Cog,
                            "backIcon",
                            tint = Color.White
                        )
                    }

                },
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(90.dp),
                containerColor = PurpleDark,       // El fondo de la barra en morado oscuro
                contentColor = Color.White, // Color base para el contenido
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                NavigationBarItem(
                    modifier = Modifier.offset(y = (-12).dp),
                    selected = false,
                    onClick = {
                        navController.navigate(route = AppScreens.Explorar.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Dumbbell, contentDescription = "Ir a explorar"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
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
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
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
                            Lucide.Search, contentDescription = "Ir a buscar"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium
                    )
                )
                // Quitamos la opcion de navegar porque ya estamos aqui
                NavigationBarItem(
                    modifier = Modifier.offset(y = (-12).dp),
                    selected = true,
                    onClick = {
                        //navController.navigate(route = AppScreens.Perfil.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.CircleUserRound,
                            contentDescription = "Perfil",
                            tint = if (true) PurpleDark else AmberGold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleDark,      // Icono oscuro para que resalte sobre el óvalo dorado
                        selectedTextColor = AmberGold,       // Texto en dorado
                        indicatorColor = AmberGold,
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold
                    )
                )
            }
        }) { innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(scrollState)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.UserRound,
                        contentDescription = "Seguidores",
                        tint = PurpleMedium
                    )
                    Text(
                        text = seguidores.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleMedium
                    )
                    Text("Seguidores", color = PurpleMedium)
                }

                Spacer(Modifier.width(150.dp))

                Column(
                    Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.UserRoundPlus,
                        contentDescription = "Siguiendo",
                        tint = PurpleMedium
                    )
                    Text(
                        text = siguiendo.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleMedium
                    )
                    Text("Siguiendo", color = PurpleMedium)
                }

            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Margen externo
                shape = RoundedCornerShape(20.dp), // Puntas redondeadas
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                // Usamos Row para poner la foto a la izquierda y el texto a la derecha
                Row(
                    modifier = Modifier
                        .padding(16.dp) // Espaciado interno
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Espacio para la foto de perfil
                    Surface(
                        modifier = Modifier
                            .size(80.dp) // Tamaño del círculo
                            .clickable { galleryLauncher.launch("image/*") },
                        shape = CircleShape,
                        border = BorderStroke(2.dp, AmberGold), // El anillo dorado
                        color = Color.LightGray // Fondo mientras no hay foto
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (loadingImage) {
                                CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(30.dp))
                            } else if (imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Lucide.User,
                                    contentDescription = "Seleccionar foto",
                                    modifier = Modifier.size(50.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Espacio entre foto y texto

                    // Datos del Usuario
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = nick,
                                color = PurpleDark,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { editarDescripcion = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Pen,
                                    contentDescription = "Editar descripcion"
                                )
                            }
                        }
                        Text(
                            text = nombreU,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = descripcion,
                            color = PurpleDark,
                        )
                    }

                    // Dialog para editar la descripcion
                    if (editarDescripcion) {
                        Dialog(onDismissRequest = { editarDescripcion = false }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp), // redondeamos los cornes para estetica
                                colors = CardDefaults.cardColors(containerColor = PurpleMedium)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Cabecera del Dialog
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Editar Perfil",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        IconButton(onClick = { editarDescripcion = false }) {
                                            Icon(
                                                imageVector = Lucide.X,
                                                contentDescription = "Cerrar",
                                                modifier = Modifier.size(24.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Campo de texto
                                    OutlinedTextField(
                                        value = descripcionNueva,
                                        onValueChange = { descripcionNueva = it },
                                        label = {
                                            Text(
                                                "Nueva descripción",
                                                color = Color.White
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AmberGold,
                                            unfocusedBorderColor = AmberGold,
                                            focusedLabelColor = AmberGold
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Botón de acción
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PurpleDark,
                                            contentColor = AmberGold
                                        ),
                                        onClick = {
                                            if (idUsuario.isNotEmpty()) {
                                                dbFirebase.collection("usuario")
                                                    .document(idUsuario)
                                                    .update("descripcion", descripcionNueva)
                                                    .addOnSuccessListener {
                                                        // Actualizamos el estado local para que se vea el cambio al instante
                                                        descripcion = descripcionNueva
                                                        editarDescripcion = false
                                                        Toast.makeText(
                                                            context,
                                                            "Perfil actualizado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Error al actualizar",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        }
                                    ) {
                                        Text("Actualizar descripción")
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
                    .padding(horizontal = 16.dp, vertical = 10.dp), // Margen externo
                shape = RoundedCornerShape(20.dp), // Puntas redondeadas
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp) // Espaciado interno
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.Dumbbell,
                        contentDescription = "Rutina",
                        tint = PurpleMedium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column() {
                        Text(
                            text = "Rutina",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleMedium
                        )
                        Text(
                            text = rutinaU,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleMedium
                        )
                    }

                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGold,
                            contentColor = PurpleMedium
                        ),
                        border = BorderStroke(1.dp, AmberGold),
                        onClick = {
                            editarRutina = true
                        }
                    ) {
                        Text(
                            text = "Cambiar Rutina",
                            color = PurpleDark, // Color temático,
                            fontSize = 20.sp,
                            //fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (editarRutina) {
                Dialog(onDismissRequest = { editarRutina = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PurpleMedium)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Seleccionar Rutina",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                IconButton(onClick = { editarRutina = false }) {
                                    Icon(
                                        Lucide.X, "Cerrar",
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            if (listaRutinas.isEmpty()) {
                                Text(
                                    "Cargando rutinas disponibles...",
                                    color = Color.White
                                )
                            } else {
                                LazyColumn {
                                    items(listaRutinas) { rutina ->
                                        val nombre = rutina["nombre"] as String
                                        val dias = rutina["Dias"]

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    dbFirebase.collection("usuario")
                                                        .document(idUsuario)
                                                        .update("rutina", nombre)
                                                        .addOnSuccessListener {
                                                            editarRutina = false
                                                            Toast.makeText(
                                                                context,
                                                                "Asignada: $nombre",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    rutinaU = nombre
                                                },
                                            colors = CardDefaults.cardColors(containerColor = PurpleDark),
                                            border = BorderStroke(1.dp, AmberGold)
                                        ) {
                                            Column(Modifier.padding(16.dp)) {
                                                Text(
                                                    nombre,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AmberGold
                                                )
                                                Text(
                                                    "Duración: $dias días",
                                                    fontSize = 12.sp,
                                                    color = AmberGold
                                                )
                                            }
                                        }
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
                    .padding(16.dp), // Margen externo para que no pegue a los bordes
                shape = RoundedCornerShape(16.dp), // Puntas redondeadas como en la imagen
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp) // Espaciado interno
                ) {
                    // Título del Calendario
                    Text(
                        text = "Calendario de Entrenamientos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Encabezados: Días y Tiempo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Días de la semana", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = "Tiempo",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista de días
                    val diasMap = listOf(
                        "L" to "Lunes",
                        "M" to "Martes",
                        "X" to "Miércoles",
                        "J" to "Jueves",
                        "V" to "Viernes",
                        "S" to "Sábado",
                        "D" to "Domingo"
                    )

                    diasMap.forEach { (letra, nombreDia) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PurpleDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = letra, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = nombreDia, fontSize = 16.sp, color = PurpleDark)
                            }

                            Surface(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(36.dp)
                                    .clickable {
                                        scope.launch {
                                            tiempoInicio = TimePickerState(
                                                initialHour = 0,
                                                initialMinute = 0,
                                                is24Hour = true
                                            )
                                            tiempoFinalizacion = TimePickerState(
                                                initialHour = 0,
                                                initialMinute = 0,
                                                is24Hour = true
                                            )

                                            diaSeleccionado = nombreDia
                                            verHoraInicio = true
                                        }
                                    },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, PurpleDark),
                                color = BackgroundGrayBlue
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = horariosSemana[nombreDia] ?: "Descanso",
                                        fontSize = 14.sp,
                                        color = PurpleDark
                                    )
                                }
                            }
                        }
                    }
                }
            }


            tiempoInicio?.let { estadoActualInicio ->
                if (verHoraInicio) {
                    seleccionHorario(
                        title = "Inicio - $diaSeleccionado",
                        state = estadoActualInicio,
                        onDismiss = { verHoraInicio = false },
                        onConfirm = {
                            tiempoFinalizacion = TimePickerState(
                                initialHour = estadoActualInicio.hour,
                                initialMinute = estadoActualInicio.minute,
                                is24Hour = true
                            )
                            verHoraInicio = false
                            verHoraFinalizacion = true
                        }
                    )
                }
            }

            tiempoFinalizacion?.let { estadoActualFin ->
                if (verHoraFinalizacion) {
                    seleccionHorario(
                        title = "Fin - $diaSeleccionado",
                        state = estadoActualFin,
                        onDismiss = { verHoraFinalizacion = false },
                        onConfirm = {
                            // Usamos !! porque dentro de este let sabemos que no son nulos
                            val hInicio = String.format("%02d:%02d", tiempoInicio!!.hour, tiempoInicio!!.minute)
                            val hFin = String.format("%02d:%02d", estadoActualFin.hour, estadoActualFin.minute)
                            val horarioCompleto = "$hInicio - $hFin"

                            horariosSemana[diaSeleccionado] = horarioCompleto
                            verHoraFinalizacion = false

                            // Guardado en Firebase
                            if (idUsuario.isNotEmpty()) {
                                dbFirebase.collection("usuario").document(idUsuario)
                                    .update("horarios.$diaSeleccionado", horarioCompleto)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Horario de $diaSeleccionado guardado", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        dbFirebase.collection("usuario").document(idUsuario)
                                            .set(mapOf("horarios" to mapOf(diaSeleccionado to horarioCompleto)),
                                                SetOptions.merge())
                                    }
                            }
                        }
                    )
                }
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
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Estadisticas musculares",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDark
                        )

                        IconButton(
                            onClick = { editarDatosFisicos = true },
                            modifier = Modifier
                                .weight(1f)
                                .size(20.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Pen,
                                contentDescription = "Editar datos fisicos"
                            )
                        }
                    }
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
                    Text("Añade marcas para ver tu nivel",
                        fontSize = 12.sp,
                        color = PurpleMedium
                    )
                }
            }

            if (editarDatosFisicos) {
                Dialog(onDismissRequest = { editarDatosFisicos = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PurpleMedium)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Cabecera
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Datos Físicos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                IconButton(onClick = { editarDatosFisicos = false }) {
                                    Icon(Lucide.X, "Cerrar", tint = Color.White)
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Altura
                            OutlinedTextField(
                                value = altura.toString(),
                                onValueChange = { if (it.isEmpty()) altura = 0 else if (it.all { char -> char.isDigit() }) altura = it.toInt() },
                                label = { Text("Altura (cm)", color = Color.White) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberGold,
                                    unfocusedBorderColor = AmberGold,
                                    focusedLabelColor = AmberGold,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            // Peso
                            var pesoTexto by remember { mutableStateOf(peso.toString()) }
                            OutlinedTextField(
                                value = pesoTexto,
                                onValueChange = { 
                                    if (it.isEmpty()) {
                                        pesoTexto = ""
                                        peso = 0.0
                                    } else if (it.matches(Regex("^\\d*[.,]?\\d*$"))) {
                                        pesoTexto = it.replace(',', '.')
                                        peso = pesoTexto.toDoubleOrNull() ?: peso
                                    }
                                },
                                label = { Text("Peso (kg)", color = Color.White) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberGold,
                                    unfocusedBorderColor = AmberGold,
                                    focusedLabelColor = AmberGold,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(Modifier.height(24.dp))

                            // Botón Actualizar
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleDark, contentColor = AmberGold),
                                onClick = {
                                    if (idUsuario.isNotEmpty()) {
                                        val datos = mapOf("altura" to altura, "peso" to peso)
                                        dbFirebase.collection("usuario").document(idUsuario)
                                            .update(datos)
                                            .addOnSuccessListener {
                                                editarDatosFisicos = false
                                                Toast.makeText(context, "Datos físicos actualizados", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                            ) {
                                Text("Actualizar datos físicos")
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDark,
                        contentColor = Color.White),
                    onClick = {
                        navController.navigate(route = AppScreens.AnadirMarcas.route + "/" + emailRecibido)
                    }
                ){
                    Text("Añade marcas a los musculos")
                }
            }
        }
    }
}

fun getColorMusculo(
    peso: Double,
    altura: Int,
    esHombre: Boolean,
    musculoSolicitado: String,
    pesoLevantado: Double
): Color {
    val purpleDark = Color(0xFF2D1B4E)
    val amberGold = Color(0xFFFFC107)

    // Lógica de fuerza base ajustada por morfología
    val factorGenero = if (esHombre) 1.0 else 0.65
    val factorAltura = if (altura > 180) 1.0 - ((altura - 180) / 150.0) else 1.0
    val fuerzaBase = peso * factorGenero * factorAltura

    val coeficientes = mapOf(
        "Trapecio" to 0.90,          // Remo en T (Máquina)
        "Deltoides" to 0.15,         // Elevaciones Laterales (Polea)
        "Hombro Posterior" to 0.11,  // Pájaros
        "Espalda Baja" to 1.10,      // Peso Muerto RDL (Suma de 2 mancuernas)
        "Cuádriceps" to 1.35,        // Sentadilla
        "Bíceps Femoral" to 0.55,    // Curl Femoral
        "Glúteo" to 1.60,            // Hip Thrust
        "Gemelo" to 1.15,            // Elevación de talones
        "Antebrazo" to 0.35,         // Curl de muñeca
        "Tríceps" to 0.42,           // Extensiones polea
        "Pecho" to 1.05,             // Press de Banca (Barra completa)
        "Bíceps" to 0.2,             // Curl Unilateral (Peso de 1 mancuerna)
        "Abductor" to 0.70,          // Máquina de Abductores
        "Abdomen" to 0.58            // Crunch en Polea Alta
    )

    val coeficiente = coeficientes[musculoSolicitado] ?: 1.0
    val pesoEsperado = fuerzaBase * coeficiente

    val ratio = if (pesoEsperado > 0) pesoLevantado / pesoEsperado else 0.0

    return when {
        pesoLevantado == 0.0 -> Color.Transparent
        ratio < 0.50 -> purpleDark                             // Nivel 1: Iniciación
        ratio < 0.70 -> lerp(purpleDark, amberGold, 0.16f)    // Nivel 2: Principiante
        ratio < 0.90 -> lerp(purpleDark, amberGold, 0.33f)    // Nivel 3: Novato+
        ratio < 1.10 -> lerp(purpleDark, amberGold, 0.50f)    // Nivel 4: Intermedio (Punto medio)
        ratio < 1.30 -> lerp(purpleDark, amberGold, 0.66f)    // Nivel 5: Intermedio+
        ratio < 1.50 -> lerp(purpleDark, amberGold, 0.83f)    // Nivel 6: Avanzado
        else         -> amberGold                             // Nivel 7: Élite / Dorado
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun seleccionHorario(
    title: String,
    state: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    val PurpleDark = Color(0xFF2D1B4E)
    val PurpleMedium = Color(0xFF4A3175)
    val AmberGold = Color(0xFFFFC107)
    val BackgroundGrayBlue = Color(0xFFDDE1E7)
    val SurfaceWhite = Color(0xFFFFFFFF)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleDark)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        selectorColor = AmberGold,
                        clockDialColor = PurpleMedium,
                        clockDialSelectedContentColor = PurpleDark,
                        clockDialUnselectedContentColor = AmberGold,
                        periodSelectorSelectedContainerColor = AmberGold
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
                    TextButton(onClick = onConfirm) { Text("Aceptar", color = Color.White) }
                }
            }
        }
    }
}
