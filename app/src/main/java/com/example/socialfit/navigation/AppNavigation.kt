package com.example.socialfit.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.socialfit.BDLocal.AppDB
import com.example.socialfit.screens.Ajustes
import com.example.socialfit.screens.AnadirMarcas
import com.example.socialfit.screens.BandejaMensajes
import com.example.socialfit.screens.Buscar
import com.example.socialfit.screens.CamaraMensajes
import com.example.socialfit.screens.Chat
import com.example.socialfit.screens.Comentarios
import com.example.socialfit.screens.Explorar
import com.example.socialfit.screens.ImagenEnviar
import com.example.socialfit.screens.InicioSesion
import com.example.socialfit.screens.Perfil
import com.example.socialfit.screens.PerfilAgeno
import com.example.socialfit.screens.Registro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun AppNavigation(){

    val context = LocalContext.current
    val navController = rememberNavController()
    
    // Estado para controlar qué pantalla mostrar al principio
    var rutaInicial by remember { mutableStateOf<String?>(null) }
    var emailSesion by remember { mutableStateOf("") }

    // BBDD Local Room para comprobar sesión
    val dbLocal = remember {
        Room.databaseBuilder(context, AppDB::class.java, "SocialFitDB").build()
    }

    LaunchedEffect(Unit) {
        val sesion = withContext(Dispatchers.IO) {
            dbLocal.sesionDao().getEstadoSesion()
        }

        if (sesion != null) {
            emailSesion = sesion.emailUsuario
            rutaInicial = AppScreens.Perfil.route + "/${sesion.emailUsuario}"
        } else {
            rutaInicial = AppScreens.InicioSesion.route
        }
    }

    // Solo mostramos el NavHost cuando ya sabemos a dónde ir
    if (rutaInicial != null) {
        NavHost(navController = navController, startDestination = rutaInicial!!) {
            
            composable(route = AppScreens.InicioSesion.route) {
                InicioSesion(navController)
            }
            
            composable(route = AppScreens.Registro.route) {
                BackHandler(true) {
                    Toast.makeText(context, "Presionaste atrás", Toast.LENGTH_SHORT).show()
                }
                Registro(navController)
            }
            
            composable(
                route = AppScreens.Perfil.route + "/{emailRecibido}",
                arguments = listOf(navArgument(name = "emailRecibido") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                Perfil(navController, backStackEntry.arguments?.getString("emailRecibido").toString())
            }

            composable(
                route = AppScreens.PerfilAgeno.route + "/{emailLocal}/{emailVisita}",
                arguments = listOf(
                    navArgument(name = "emailLocal") {
                        type = NavType.StringType
                    },
                    navArgument(name = "emailVisita") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                PerfilAgeno(navController, backStackEntry.arguments?.getString("emailLocal").toString(),
                    backStackEntry.arguments?.getString("emailVisita").toString())
            }

            composable(
                route = AppScreens.Ajustes.route + "/{emailRecibido}",
                arguments = listOf(navArgument(name = "emailRecibido") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                Ajustes(navController, backStackEntry.arguments?.getString("emailRecibido").toString())
            }

            composable(
                route = AppScreens.AnadirMarcas.route + "/{emailRecibido}",
                arguments = listOf(navArgument(name = "emailRecibido") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                AnadirMarcas(navController, backStackEntry.arguments?.getString("emailRecibido").toString())
            }

            composable(
                route = AppScreens.Buscar.route + "/{emailRecibido}",
                arguments = listOf(navArgument(name = "emailRecibido") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                Buscar(navController, backStackEntry.arguments?.getString("emailRecibido").toString())
            }

            composable(
                route = AppScreens.BandejaMensajes.route + "/{emailRecibido}",
                arguments = listOf(navArgument(name = "emailRecibido") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                BandejaMensajes(navController, backStackEntry.arguments?.getString("emailRecibido").toString())
            }

            composable(
                route = AppScreens.Chat.route + "/{emailLocal}/{emailVisita}",
                arguments = listOf(
                    navArgument(name = "emailLocal") {
                        type = NavType.StringType
                    },
                    navArgument(name = "emailVisita") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                Chat(navController, backStackEntry.arguments?.getString("emailLocal").toString(),
                    backStackEntry.arguments?.getString("emailVisita").toString())
            }

            composable(
                route = AppScreens.Explorar.route + "/{emailRecibido}",
                arguments = listOf(navArgument(name = "emailRecibido") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                Explorar(navController, backStackEntry.arguments?.getString("emailRecibido").toString())
            }

            composable(
                route = "ImagenEnviar/{emailLocal}/{emailVisita}/{imageUri}",
                arguments = listOf(
                    navArgument("emailLocal") { type = NavType.StringType },
                    navArgument("emailVisita") { type = NavType.StringType },
                    navArgument("imageUri") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val emailLocal = backStackEntry.arguments?.getString("emailLocal") ?: ""
                val emailVisita = backStackEntry.arguments?.getString("emailVisita") ?: ""
                val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""

                ImagenEnviar(navController, emailLocal, emailVisita, imageUri)
            }

            composable(
                route = AppScreens.CamaraMensajes.route + "/{emailLocal}/{emailVisita}",
                arguments = listOf(
                    navArgument(name = "emailLocal") {
                        type = NavType.StringType
                    },
                    navArgument(name = "emailVisita") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                CamaraMensajes(navController, backStackEntry.arguments?.getString("emailLocal").toString(),
                    backStackEntry.arguments?.getString("emailVisita").toString())
            }

            composable(
                route = AppScreens.Comentarios.route + "/{idDoc}/{emailLocal}",
                arguments = listOf(
                    navArgument("idDoc") { type = NavType.StringType },
                    navArgument("emailLocal") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val idDoc = backStackEntry.arguments?.getString("idDoc") ?: ""
                val emailLocal = backStackEntry.arguments?.getString("emailLocal") ?: ""
                Comentarios(navController, idDoc, emailLocal)
            }
        }
    }
}
