package com.example.socialfit.navigation

sealed class AppScreens (val route: String) {
    object InicioSesion: AppScreens("InicioSesion")
    object Registro: AppScreens("Registro")
}
