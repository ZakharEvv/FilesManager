package com.example.filesmanager.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.filesmanager.model.FileItem

@Database(entities = [FileItem::class], version = 1, exportSchema = false)
abstract class FilesDatabase : RoomDatabase() {

    abstract fun filesDao() : FilesDao

    companion object {

        private var instance : FilesDatabase? = null

        fun getInstance(application: Application) : FilesDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    application,
                    FilesDatabase::class.java,
                    "files.db").build()
            return instance as FilesDatabase
        }
    }
}