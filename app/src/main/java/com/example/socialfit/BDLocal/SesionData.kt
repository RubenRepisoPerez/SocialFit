package com.example.socialfit.BDLocal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity( // Marca la clase como una entidad que corresponde a una tabla en la BD, accediendo por el nombre. Además, se crea la clave foránea con foreignKeys.
    tableName = Estructura.Sesion.TABLE_NAME,
)
data class SesionData(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = Estructura.Sesion.IDSESION) val idSesion: Int = 0,
    @ColumnInfo(name = Estructura.Sesion.IDUSUARIO) val idUsuario: String,
    @ColumnInfo(name = Estructura.Sesion.EMAILUSUARIO) val emailUsuario: String
)