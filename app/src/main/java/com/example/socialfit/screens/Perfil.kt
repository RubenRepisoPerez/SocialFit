package com.example.socialfit.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                        //navController.popBackStack()
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

        Column(modifier = Modifier.padding(innerPadding)
            .verticalScroll(scrollState)) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.UserRound,
                        contentDescription = "Seguidores",
                        tint = PurpleMedium
                    )
                    Text(text = seguidores.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleMedium
                        )
                    Text("Seguidores", color = PurpleMedium)
                }

                Spacer(Modifier.width(150.dp))

                Column(Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Lucide.UserRoundPlus,
                        contentDescription = "Siguiendo",
                        tint = PurpleMedium
                    )
                    Text(text = siguiendo.toString(),
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
                            IconButton(onClick = {editarDescripcion = true},
                                modifier = Modifier
                                    .weight(1f)
                                    .size(20.dp)) {
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
                                        label = { Text("Nueva descripción",
                                            color = Color.White) },
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
                                                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
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
            ){
                Row(modifier = Modifier
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
                    Spacer( modifier = Modifier.width(16.dp))
                    Column() {
                        Text(text = "Rutina",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleMedium
                        )
                        Text(text = rutinaU,
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
                        Column(modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Seleccionar Rutina",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White)
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
                                    color = Color.White)
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
                                                Text(nombre, fontWeight = FontWeight.Bold, color = AmberGold)
                                                Text("Duración: $dias días", fontSize = 12.sp, color = AmberGold)
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
                        Text(text = "Tiempo", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(end = 40.dp))
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
                                    Text(text = dia, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                if(dia == "L"){
                                    diaLargo = "Lunes"
                                }
                                if(dia == "M"){
                                    diaLargo = "Martes"
                                }
                                if(dia == "X"){
                                    diaLargo = "Miercoles"
                                }
                                if(dia == "J"){
                                    diaLargo = "Jueves"
                                }
                                if(dia == "V"){
                                    diaLargo = "Viernes"
                                }
                                if(dia == "S"){
                                    diaLargo = "Sabado"
                                }
                                if(dia == "D"){
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
        }
    }
}