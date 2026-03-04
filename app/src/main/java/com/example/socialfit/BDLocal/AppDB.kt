package com.example.socialfit.BDLocal

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SesionData::class], 
    version = 1, 
    exportSchema = true
)
abstract class AppDB : RoomDatabase() {
    abstract fun sesionDao(): SesionDao
}
