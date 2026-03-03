package com.example.socialfit.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.socialfit.screens.InicioSesion
import com.example.socialfit.screens.Perfil
import com.example.socialfit.screens.PerfilAgeno
import com.example.socialfit.screens.Registro


@Composable
fun AppNavigation(){

    val context = LocalContext.current

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.InicioSesion.route) {
        //NavHost(navController = navController, startDestination = if(estadoSesion == null)AppScreens.Inicio.route else AppScreens.Formulario.route){
        composable (route = AppScreens.InicioSesion.route){
            InicioSesion(navController)
        }
        composable (route = AppScreens.Registro.route){
            BackHandler(true) {
                Toast.makeText(context, "Presionaste atrás, pero está restringido volver atrás", Toast.LENGTH_SHORT).show()
            }
            Registro(navController)
        }
        composable (route = AppScreens.Perfil.route + "/{emailRecibido}",
            arguments = listOf(navArgument(name = "emailRecibido"){
                type = NavType.StringType
            })){
            Perfil(navController, it.arguments?.getString("emailRecibido").toString())
        }
        composable (route = AppScreens.PerfilAgeno.route){
            PerfilAgeno(navController)
        }
    }
}