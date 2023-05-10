package com.example.filesmanager

import com.example.filesmanager.model.FileItem

object RecentFilesStore {

    private var files = ArrayList<FileItem>()

    fun setRecentFiles(array : ArrayList<FileItem>){
        files.addAll(array)
    }

    fun getRecentFiles() : ArrayList<FileItem>{
        return files
    }

}