package com.example.socialfit.BDLocal

class Estructura {

    //BASE DE DATOS
    object DB {
        const val NAME = "Usuarios.db"
    }

    //TABLA 2: Sesion
    object Sesion {
        const val TABLE_NAME = "SESIONES"
        const val IDSESION = "idSesion"
        const val IDUSUARIO = "idUsuario"
        const val EMAILUSUARIO = "emailUsuario"
    }

}