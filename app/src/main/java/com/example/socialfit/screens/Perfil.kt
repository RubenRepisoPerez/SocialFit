package com.example.socialfit.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.Cog
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pen
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.composables.icons.lucide.UserRound
import com.composables.icons.lucide.UserRoundPlus
import com.composables.icons.lucide.X
import com.example.socialfit.FirebaseTemplate
import com.example.socialfit.R
import com.example.socialfit.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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
    val dbFirebase = Firebase.firestore
    val scope = rememberCoroutineScope()

    var idUsuario by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var nick by remember { mutableStateOf("") }
    var nombreU by remember { mutableStateOf("") }
    var seguidores by remember { mutableStateOf(0) }
    var siguiendo by remember { mutableStateOf(0)}
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


    // Lista rutinas
    var listaRutinas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

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
                    if (document != null) {
                        descripcion = document.getString("descripcion") ?: "Sin descripción"
                        // seguidores = document.getLong("seguidores")?.toInt() ?: 0
                        nick = document.getString("nick") ?: "Sin nick"
                        nombreU = document.getString("nombre") ?: "Sin nombre"
                        rutinaU = document.getString("rutina") ?: "Sin rutina"
                        sexo = document.getString("sexo") ?: "Sin sexo"
                        if(sexo == "Hombre") {
                            sexoBoolean = true
                        }
                        if(sexo == "Mujer") {
                            sexoBoolean = false
                        }
                        altura = document.getLong("altura")?.toInt() ?: 0
                        peso = document.getDouble("peso") ?: 0.0 // Leemos como Double
                    }
                }
        } else {
            descripcion = "Usuario no encontrado"
        }
    }

    val context = LocalContext.current
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
                    Text(text = "Mi Perfil", fontSize = 30.sp, color = Color.White, fontWeight = FontWeight.Bold)
                },

                colors = topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = Color.White),

                actions = {
                    IconButton(onClick = {
                        navController.navigate(route = AppScreens.Ajustes.route + "/" + emailRecibido)
                        Toast.makeText(context, "Volver atrás", Toast.LENGTH_SHORT).show()
                    }
                    ) {
                        Icon(Lucide.Cog, "backIcon", tint = Color.White)
                    }

                }
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.height(90.dp),
                containerColor = PurpleDark,       // El fondo de la barra en morado oscuro
                contentColor = Color.White         // Color base para el contenido
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.MisInmuebles.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Dumbbell, contentDescription = "Ir a explorar"
                        )
                    },
                    label = { Text("Explorar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.Mensajes.route)
                    },
                    icon = {
                        Icon(
                            Lucide.Text, contentDescription = "Ir a mensajes"
                        )
                    },
                    label = { Text("Mensajes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.Productos.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.Search, contentDescription = "Ir a buscar"
                        )
                    },
                    label = { Text("Buscar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        //navController.navigate(route = AppScreens.Resultados.route + "/" + emailRecibido)
                    },
                    icon = {
                        Icon(
                            Lucide.CircleUserRound, contentDescription = "Ir al perfil de usuario"
                        )
                    },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberGold,   // Icono seleccionado en Dorado
                        selectedTextColor = AmberGold,   // Texto seleccionado en Dorado
                        unselectedIconColor = AmberGold,
                        unselectedTextColor = AmberGold,
                        indicatorColor = PurpleMedium     // El "óvalo" detrás del icono seleccionado
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
                        modifier = Modifier.size(80.dp), // Tamaño del círculo
                        shape = CircleShape,
                        border = BorderStroke(2.dp, AmberGold), // El anillo dorado
                        color = Color.LightGray // Fondo mientras no hay foto
                    ) {
                        // Aquí iría tu Image(...)
                        // Image(painter = ..., contentDescription = "Foto de perfil", contentScale = ContentScale.Crop)
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // Espacio entre foto y texto

                    // Datos del Usuario
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = nick,
                                color = PurpleDark, // Color temático,
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
                    val dias = listOf("L", "M", "X", "J", "V", "S", "D")
                    var diaLargo = ""
                    dias.forEach { dia ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Parte izquierda: Icono morado y Letra del día
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PurpleDark), // Cuadrado morado para el día
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dia,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                if (dia == "L") {
                                    diaLargo = "Lunes"
                                }
                                if (dia == "M") {
                                    diaLargo = "Martes"
                                }
                                if (dia == "X") {
                                    diaLargo = "Miercoles"
                                }
                                if (dia == "J") {
                                    diaLargo = "Jueves"
                                }
                                if (dia == "V") {
                                    diaLargo = "Viernes"
                                }
                                if (dia == "S") {
                                    diaLargo = "Sabado"
                                }
                                if (dia == "D") {
                                    diaLargo = "Domingo"
                                }
                                Text(text = diaLargo, fontSize = 16.sp, color = PurpleDark)
                            }

                            Surface(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, PurpleDark),
                                color = BackgroundGrayBlue
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "17:00 - 18:30",
                                        fontSize = 14.sp,
                                        color = PurpleDark
                                    )
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
                            var colorBiceps = getColorMusculo(peso.toDouble(), altura, sexoBoolean, "Biceps", pesoBiceps)
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
                            modifier = Modifier.background(Color.Transparent).
                            padding(top = 35.dp)
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

    val factorGenero = if (esHombre) 1.0 else 0.65
    val factorAltura = if (altura > 180) 1.0 - ((altura - 180) / 150.0) else 1.0
    val fuerzaBase = peso * factorGenero * factorAltura

    // Coeficientes actualizados
    val coeficientes = mapOf(
        "Trapecio" to 0.95,          // Remo en T
        "Deltoides" to 0.14,         // Elevaciones Laterales
        "Hombro Posterior" to 0.11,  // Pájaros
        "Espalda Baja" to 1.25,      // Peso Muerto Rumano
        "Cuádriceps" to 1.35,        // Sentadilla
        "Bíceps Femoral" to 0.55,    // Curl Femoral
        "Glúteo" to 1.60,            // Hip Thrust
        "Gemelo" to 1.15,            // Elevación de talones
        "Antebrazo" to 0.35,         // Curl de muñeca
        "Tríceps" to 0.42,           // Extensiones polea
        "Pecho" to 1.05,             // Press de Banca (Equilibrado)
        "Bíceps" to 0.40,            // Curl de Bíceps (Barra/Mancuerna)
        "Abductor" to 0.70           // Máquina de Abductores
    )

    val coeficiente = coeficientes[musculoSolicitado] ?: 1.0
    val pesoEsperado = fuerzaBase * coeficiente

    val ratio = if (pesoEsperado > 0) pesoLevantado / pesoEsperado else 0.0

    return when {
        pesoLevantado == 0.0 -> Color.Transparent
        ratio < 0.70 -> purpleDark
        ratio < 0.90 -> lerp(purpleDark, amberGold, 0.25f)
        ratio <= 1.10 -> lerp(purpleDark, amberGold, 0.50f)
        ratio <= 1.30 -> lerp(purpleDark, amberGold, 0.75f)
        else -> amberGold
    }
}