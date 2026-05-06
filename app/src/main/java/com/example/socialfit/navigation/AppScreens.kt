package com.example.socialfit.navigation

sealed class AppScreens (val route: String) {
    object InicioSesion: AppScreens("InicioSesion")
    object Registro: AppScreens("Registro")
    object Perfil: AppScreens("Perfil")
    object CamaraMensajes: AppScreens("CamaraMensajes")
    object ImagenEnviar: AppScreens("ImagenEnviar")
    object Explorar: AppScreens("Explorar")
    object Chat: AppScreens("Chat")
    object BandejaMensajes: AppScreens("BandejaMensajes")
    object Buscar: AppScreens("Buscar")
    object AnadirMarcas: AppScreens("AnadirMarcas")
    object Ajustes: AppScreens("Ajustes")
    object PerfilAgeno: AppScreens("PerfilAgeno")
    object Comentarios: AppScreens("Comentarios")
}
