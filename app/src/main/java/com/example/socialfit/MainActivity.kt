package com.example.socialfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.socialfit.navigation.AppNavigation
import com.example.socialfit.notificacion.GestorNotificaciones
import com.example.socialfit.ui.theme.SocialFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialFitTheme {
                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                var idUsuarioReal by remember { mutableStateOf("") }

                LaunchedEffect(user) {
                    if (user != null && user.email != null) {
                        val idEncontrado = FirebaseTemplate.obtenerIdConEmail(user.email!!)
                        if (idEncontrado != null) {
                            idUsuarioReal = idEncontrado
                        }
                    }
                }

                if (idUsuarioReal.isNotEmpty()) {
                    GestorNotificaciones(idUsuario = idUsuarioReal)
                }

                AppNavigation()
            }
        }
    }
}
