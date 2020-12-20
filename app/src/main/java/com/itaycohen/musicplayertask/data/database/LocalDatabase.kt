package com.itaycohen.musicplayertask.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.itaycohen.musicplayertask.data.database.dao.AudioDao
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.models.AudioItemIndex

@Database(
    entities = [
        AudioItem::class,
        AudioItemIndex::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun audioDao() : AudioDao

    companion object {
        private lateinit var instance: LocalDatabase

        fun getInstance(appContext: Context) =
            Room.databaseBuilder(appContext, LocalDatabase::class.java, "LOCAL_DATABASE")
                .build()
    }
}