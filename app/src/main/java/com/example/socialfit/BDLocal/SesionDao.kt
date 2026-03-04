package com.example.socialfit.BDLocal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SesionDao {
    // Iniciar sesion
    @Insert
    fun nuevaSesion(user: SesionData)

    // Consultar inicio de sesión
    @Query("SELECT * FROM ${Estructura.Sesion.TABLE_NAME} LIMIT 1")
    fun getEstadoSesion(): SesionData?

    // Borrar sesión anterior
    @Query("DELETE FROM ${Estructura.Sesion.TABLE_NAME}")
    fun borrarSesionAnterior()
}
