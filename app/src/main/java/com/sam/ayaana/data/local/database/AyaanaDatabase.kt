package com.sam.ayaana.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sam.ayaana.data.local.dao.ActivityDao
import com.sam.ayaana.data.local.dao.ChatDao
import com.sam.ayaana.data.local.dao.PostDao
import com.sam.ayaana.data.local.dao.ReelDao
import com.sam.ayaana.data.local.dao.UserDao
import com.sam.ayaana.data.local.entity.ActivityEntity
import com.sam.ayaana.data.local.entity.ChatEntity
import com.sam.ayaana.data.local.entity.MessageEntity
import com.sam.ayaana.data.local.entity.PostEntity
import com.sam.ayaana.data.local.entity.ReelEntity
import com.sam.ayaana.data.local.entity.SearchHistoryEntity
import com.sam.ayaana.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, PostEntity::class, ActivityEntity::class, ChatEntity::class, MessageEntity::class, ReelEntity::class, SearchHistoryEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AyaanaDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun activityDao(): ActivityDao
    abstract fun chatDao(): ChatDao
    abstract fun reelDao(): ReelDao


    companion object{
        const val DATABASE_NAME = "ayaana_database"
    }

}