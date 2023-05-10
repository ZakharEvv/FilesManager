package com.example.filesmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files_db")
data class FileItem(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name : String,
    val date : String,
    val size : Float,
    val fileIcon : Int,
    val absolutePath : String,
    val hash : String
)
