package com.example.filesmanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.filesmanager.model.FileItem

@Dao
interface FilesDao {

    @Query("SELECT * FROM files_db")
    fun getAllFiles() : List<FileItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(fileItem: FileItem)

    @Query("DELETE FROM files_db")
    fun deleteAll()
}