package com.example.filesmanager.viewmodels

import android.app.Application
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.filesmanager.FileHelper
import com.example.filesmanager.database.FilesDatabase
import com.example.filesmanager.model.FileItem
import kotlinx.coroutines.*
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Collections

@RequiresApi(Build.VERSION_CODES.O)
class InternalViewModel(application: Application) : AndroidViewModel(application){

    private var filesList = ArrayList<FileItem>()
    val files = MutableLiveData<List<FileItem>>()
    private val rootPath = java.lang.StringBuilder()
    private var file : File
    private var block : String
    var sortType = ""

    private var database: FilesDatabase

    init {
        rootPath.append(Environment.getExternalStorageDirectory().absolutePath)
        block = rootPath.substring(0, rootPath.lastIndexOf("/"))
        file = File(rootPath.toString())
        file.listFiles()?.let { updateList(it) }
        database = FilesDatabase.getInstance(application)
    }

    //sets a start path for categories on the main screen
    fun setStartFrom(path : String){
        rootPath.append(path)
        block = rootPath.substring(0, rootPath.lastIndexOf("/"))
        file = File(rootPath.toString())
        file.listFiles()?.let { updateList(it) }
    }

    //method for onFileItem click. Goes into a path
    @RequiresApi(Build.VERSION_CODES.O)
    fun goIntoPath(name : String){
        rootPath.append("/$name")
        file = File(rootPath.toString())
        file.listFiles()?.let { updateList(it) }
    }

    //goes back from the path
    @RequiresApi(Build.VERSION_CODES.O)
    fun goBack() : Boolean{
        var isEnd = true
        val newPath = rootPath.substring(0, rootPath.lastIndexOf("/"))
        if (!newPath.equals(block)) {
            rootPath.clear()
            rootPath.append(newPath)
            file = File(rootPath.toString())
            file.listFiles()?.let { updateList(it) }
            isEnd = false
        }
        return isEnd
    }

    //refreshes files array for adapter
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateList(filesParam : Array<File>){
        filesList.clear()
        for(file in filesParam){
            filesList.add(FileHelper.convertFileToFileItem(file))
        }
        Collections.sort(filesList, FileComparator())
        sortFiles(sortType)
    }

    //sets a type of sorting files
    fun setTypeSort(sortType : String){
        this.sortType = sortType
        sortFiles(sortType)
    }

    //sorts files by a sortType param
    fun sortFiles(sortType : String){
        var sorted = ArrayList<FileItem>()

        when(sortType){
            "date" -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                sorted = ArrayList(filesList.sortedBy {
                    LocalDate.parse(it.date, formatter)
                })
            }
            "dateDescending" -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                sorted = ArrayList(filesList.sortedByDescending {
                    LocalDate.parse(it.date, formatter)
                })
            }
            "size" -> {
                sorted = ArrayList(filesList.sortedBy {
                    it.size
                })
            }
            "sizeDescending" -> {
                sorted = ArrayList(filesList.sortedByDescending {
                    it.size
                } )
            }
            ".txt" -> {
                sorted.addAll(ArrayList(filesList.filter { it.name.endsWith(".txt") }))
            }
            ".png" -> {
                sorted.addAll(ArrayList(filesList.filter { it.name.endsWith(".png") }))
            }
            ".pdf" -> {
                sorted.addAll(ArrayList(filesList.filter { it.name.endsWith(".pdf") }))
            }
            ".mp3" -> {
                sorted.addAll(ArrayList(filesList.filter { it.name.endsWith(".mp3") }))
            }
            "" -> {
                sorted = filesList
            }
        }
        files.value = sorted
    }

    internal class FileComparator : Comparator<FileItem?> {
        override fun compare(o1: FileItem?, o2: FileItem?): Int {
            return o1!!.name.compareTo(o2!!.name)
        }
    }
}