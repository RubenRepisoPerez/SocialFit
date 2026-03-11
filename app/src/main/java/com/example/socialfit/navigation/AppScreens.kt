package com.example.socialfit.navigation

sealed class AppScreens (val route: String) {
    object InicioSesion: AppScreens("InicioSesion")
    object Registro: AppScreens("Registro")
    object Perfil: AppScreens("Perfil")
    object PerfilAgeno: AppScreens("PerfilAgeno")
    object Ajustes: AppScreens("Ajustes")
    object AnadirMarcas: AppScreens("AnadirMarcas")
    object Buscar: AppScreens("Buscar")
    object BandejaMensajes: AppScreens("BandejaMensajes")
}
