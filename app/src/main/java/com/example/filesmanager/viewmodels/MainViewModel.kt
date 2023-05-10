package com.example.filesmanager.viewmodels

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.filesmanager.FileHelper
import com.example.filesmanager.database.FilesDatabase
import com.example.filesmanager.model.FileItem
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var rootPath = java.lang.StringBuilder()
    val recent = MutableLiveData<ArrayList<FileItem>>()
    private var recentList = ArrayList<FileItem>()
    private val database : FilesDatabase

    init {
        rootPath.append(Environment.getExternalStorageDirectory().absolutePath)
        database = FilesDatabase.getInstance(application)
    }

    //inserts hash of all files to compare in the next application start
    fun insertAllFilesHash() {
        Thread {
            val directory = File(rootPath.toString())
            val files = directory.walkTopDown().filter { it.isFile }.toList()
            database.filesDao().deleteAll()
            for(file in files) {
                if (isDocument(file.name)) {
                    try {
                        database.filesDao().insertFile(FileHelper.convertFileToFileItem(file))
                    } catch (_: Exception) { }
                }
            }
        }.start()

    }

    //compare the files in device storage with the files in db and add changed files in ArrayList
    fun checkRecentFiles() {
        Thread {
            recentList = ArrayList()
            val savedArray = database.filesDao().getAllFiles()

            var paths : Path
            var data : ByteArray
            var hash : ByteArray
            var checkSum : String

            for(file in savedArray){
                if (isDocument(file.name)) {
                    paths = Paths.get(file.absolutePath)
                    if (Files.exists(paths)) {
                        try {
                            data = Files.readAllBytes(paths)
                            hash = MessageDigest.getInstance("MD5").digest(data)
                            checkSum = BigInteger(1, hash).toString(16)
                            if (file.hash != checkSum)
                                recentList.add(file)
                        } catch (_: Exception){}
                    }
                }
            }
            recent.postValue(recentList)
        }.start()
    }

    //checks if file is editable
    fun isDocument(name : String) : Boolean {
        var isDoc = false
        if(name.contains(".")) {
            val extension = name.substring(name.lastIndexOf("."), name.length)
            val listExtensions =
                listOf(".txt", ".doc", ".docx", ".pptx", ".xls", ".xlsx", ".ppt", ".pptx")
            isDoc = false



            if (extension in listExtensions) {
                isDoc = true
            }
        }
        return isDoc
    }
}