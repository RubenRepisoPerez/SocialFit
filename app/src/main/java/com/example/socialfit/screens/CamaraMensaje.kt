package com.example.socialfit.screens

import android.content.Context
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
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CamaraMensajes(navController: NavController, emailLocal: String, emailVisita: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val ejecutorCamara = remember { Executors.newSingleThreadExecutor() }

    val permisoEstado = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        permisoEstado.launchPermissionRequest()
    }

    var apuntarLente by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imagenCapturada = remember { ImageCapture.Builder().build() }
    val contenedorVistaPrevia = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    if (!permisoEstado.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
        return
    }

    // Launcher galería
    val abrirGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val encodedUri = Uri.encode(it.toString())
            navController.navigate(AppScreens.ImagenEnviar.route + "/$emailLocal/$emailVisita/$encodedUri")
        }
    }

    LaunchedEffect(apuntarLente, permisoEstado.status.isGranted) {
        if (!permisoEstado.status.isGranted) return@LaunchedEffect

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val casoUsoPreview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(contenedorVistaPrevia.surfaceProvider)
                    }

                val selecctorCamara = CameraSelector.Builder()
                    .requireLensFacing(apuntarLente)
                    .build()

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selecctorCamara,
                    casoUsoPreview,
                    imagenCapturada
                )
            } catch (e: Exception) {
                Log.e("CamaraFit", "Error al inicializar CameraX: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(
        modifier = Modifier.fillMaxSize().
        background(Color.Black)
    ) {
        AndroidView(
            factory = { contenedorVistaPrevia },
            modifier = Modifier.fillMaxSize(),
            update = { _ ->

            }
        )

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 40.dp, start = 16.dp).
            align(Alignment.TopStart)
        ) {
            Icon(
                Lucide.X,
                contentDescription = "Cerrar",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Barra de controles inferior
        Row(
            modifier = Modifier.fillMaxWidth().
            align(Alignment.BottomCenter).
            padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Galería
            IconButton(
                onClick = { abrirGaleria.launch("image/*") }
            ) {
                Icon(
                    Lucide.Image,
                    contentDescription = "Galería",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Hacer Foto
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .padding(6.dp)
                    .background(Color.White, CircleShape)
                    .clickable {
                        tomarFoto(imagenCapturada, context, ejecutorCamara) { uri ->
                            val encodedUri = Uri.encode(uri.toString())
                            Handler(Looper.getMainLooper()).post {
                                navController.navigate(AppScreens.ImagenEnviar.route + "/$emailLocal/$emailVisita/$encodedUri")
                            }
                        }
                    }
            )

            // Voltear cámara
            IconButton(onClick = {
                apuntarLente = if (apuntarLente == CameraSelector.LENS_FACING_BACK)
                    CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
            }) {
                Icon(
                    Lucide.RefreshCw,
                    contentDescription = "Voltear",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
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
