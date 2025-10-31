package com.sam.ayaana.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sam.ayaana.data.local.dao.ActivityDao
import com.sam.ayaana.data.local.dao.PostDao
import com.sam.ayaana.data.local.dao.UserDao
import com.sam.ayaana.data.local.entity.ActivityEntity
import com.sam.ayaana.data.local.entity.PostEntity
import com.sam.ayaana.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, PostEntity::class, ActivityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AyaanaDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun activityDao(): ActivityDao

    companion object{
        const val DATABASE_NAME = "ayaana_database"
    }

}