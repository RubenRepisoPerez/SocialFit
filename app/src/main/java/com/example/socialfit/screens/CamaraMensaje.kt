package com.example.socialfit.screens

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.X
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.compose.foundation.clickable
import com.example.socialfit.navigation.AppScreens
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.PermissionStatus
import android.os.Handler
import android.os.Looper
import androidx.camera.video.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CamaraMensajes(navController: NavController, emailLocal: String, emailVisita: String) {

    val PurpleDark = Color(0xFF2D1B4E)
    val AmberGold = Color(0xFFFFC107)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val ejecutorCamara = remember { Executors.newSingleThreadExecutor() }

    // Estado para diferenciar entre foto y vídeo
    var esGrabando by remember { mutableStateOf(false) }
    var grabacionActual: Recording? by remember { mutableStateOf(null) }
    var modoSeleccionado by remember { mutableStateOf("Foto") } // "Foto" o "Vídeo"
    val scope = rememberCoroutineScope()

    // Caso de uso para Video
    val videoCapture = remember {
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        VideoCapture.withOutput(recorder)
    }

    val permisoEstado = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    // Audio para el vídeo
    val permisoAudio = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        permisoEstado.launchPermissionRequest()
        permisoAudio.launchPermissionRequest()
    }

    var apuntarLente by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imagenCapturada = remember { ImageCapture.Builder().build() }
    val contenedorVistaPrevia = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }

    // Launcher galería actualizado para IMAGEN Y VÍDEO
    // Función auxiliar para copiar el archivo inmediatamente
    fun crearCopiaTemporal(context: Context, uriOriginal: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uriOriginal)
            val tempFile = File(context.cacheDir, "post_temp_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            null
        }
    }

    val abrirGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // COPIAMOS EL ARCHIVO AQUÍ MISMO
            val uriSegura = crearCopiaTemporal(context, it)
            if (uriSegura != null) {
                val encodedUri = Uri.encode(uriSegura.toString())
                navController.navigate(route = AppScreens.ImagenEnviar.route + "/$emailLocal/$emailVisita/$encodedUri")
            }
        }
    }

    LaunchedEffect(apuntarLente, permisoEstado.status.isGranted) {
        if (!permisoEstado.status.isGranted) return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val casoUsoPreview = Preview.Builder().build().also {
                it.setSurfaceProvider(contenedorVistaPrevia.surfaceProvider)
            }
            val selecctorCamara = CameraSelector.Builder().requireLensFacing(apuntarLente).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, selecctorCamara, casoUsoPreview, imagenCapturada, videoCapture
                )
            } catch (e: Exception) { Log.e("CamaraFit", "Error bind: ${e.message}") }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
        AndroidView(factory = { contenedorVistaPrevia }, modifier = Modifier.fillMaxSize())

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp) // Ajusta para que esté sobre el botón
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Foto", "Vídeo").forEach { modo ->
                val esActivo = modoSeleccionado == modo
                Box(
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .background(if (esActivo) AmberGold else Color.Transparent)
                        .clickable { modoSeleccionado = modo }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = modo,
                        color = if (esActivo) PurpleDark else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Botón disparo con lógica de Vídeo y Gestos
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .size(90.dp)
                .border(4.dp, if (esGrabando) Color.Red else Color.White, CircleShape)
                .padding(8.dp)
                .background(if (esGrabando) Color.Red else Color.White, CircleShape)
                .pointerInput(modoSeleccionado) {
                    // Usamos coroutines para manejar ambos detectores en paralelo
                    val scope = this

                    scope.detectTapGestures(
                        onTap = {
                            if (modoSeleccionado == "Foto") {
                                tomarFoto(imagenCapturada, context, ejecutorCamara) { uri ->
                                    navegarAEnviar(navController, emailLocal, emailVisita, uri)
                                }
                            } else {
                                if (!esGrabando) {
                                    esGrabando = true
                                    grabacionActual = grabarVideo(videoCapture, context, ejecutorCamara) { uri ->
                                        navegarAEnviar(navController, emailLocal, emailVisita, uri)
                                    }
                                } else {
                                    grabacionActual?.stop()
                                    esGrabando = false
                                }
                            }
                        },
                        onLongPress = {
                            if (!esGrabando) {
                                esGrabando = true
                                grabacionActual = grabarVideo(videoCapture, context, ejecutorCamara) { uri ->
                                    navegarAEnviar(navController, emailLocal, emailVisita, uri)
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.all { it.changedToUp() }) {
                                if (esGrabando) {
                                    grabacionActual?.stop()
                                    esGrabando = false
                                }
                            }
                        }
                    }
                }
        )

        // Botón Galería
        IconButton(
            onClick = { abrirGaleria.launch("*/*") },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 56.dp, start = 32.dp)
        ) {
            Icon(
                Lucide.Image,
                contentDescription = "Galería",
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )
        }

        // Botón Volver Atrás
        IconButton(
            onClick = { navController.popBackStack() },modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 20.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Lucide.X,
                contentDescription = "Cerrar",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        // Botón Girar Cámara
        IconButton(
            onClick = {
                apuntarLente = if (apuntarLente == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 48.dp, end = 20.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Lucide.RefreshCw,
                contentDescription = "Girar Cámara",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

// Función auxiliar para navegar
private fun navegarAEnviar(navController: NavController, local: String, visita: String, uri: Uri) {
    val encodedUri = Uri.encode(uri.toString())
    Handler(Looper.getMainLooper()).post {
        navController.navigate(AppScreens.ImagenEnviar.route + "/$local/$visita/$encodedUri")
    }
}

private fun grabarVideo(
    videoCapture: VideoCapture<Recorder>,
    context: Context,
    executor: ExecutorService,
    onVideoSaved: (Uri) -> Unit
): Recording {
    val archivoVideo = File(context.externalCacheDir, "VID_${System.currentTimeMillis()}.mp4")
    val outputOptions = FileOutputOptions.Builder(archivoVideo).build()

    val pendingRecording = videoCapture.output
        .prepareRecording(context, outputOptions)

    if (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        pendingRecording.withAudioEnabled()
    }

    return pendingRecording.start(executor) { event ->
        if (event is VideoRecordEvent.Finalize) {
            if (!event.hasError()) {
                onVideoSaved(Uri.fromFile(archivoVideo))
            } else {
                Log.e("CamaraFit", "Error video: ${event.error}")
            }
        }
    }
}

private fun tomarFoto(imageCapture: ImageCapture, context: Context, executor: ExecutorService, onPhotoTaken: (Uri) -> Unit) {
    val archivoFoto = File(context.externalCacheDir, "IMG_${System.currentTimeMillis()}.jpg")
    val opcionesSalida =
        ImageCapture.OutputFileOptions.Builder(archivoFoto).build()

    imageCapture.takePicture(opcionesSalida, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            val uriGuardada = Uri.fromFile(archivoFoto)
            onPhotoTaken(uriGuardada)
        }
        override fun onError(exception: ImageCaptureException) {
            Log.e("CamaraFit", "Error al capturar imagen", exception)
        }
    })
}
