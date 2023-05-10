package com.example.filesmanager

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.filesmanager.model.FileItem
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest

class FileHelper {

    companion object{
        //receives a file param and converts it to an object of FileItem class
        fun convertFileToFileItem(file: File) : FileItem {
            val paths = Paths.get(file.absolutePath)
            val attributes = Files.readAttributes(paths, BasicFileAttributes::class.java)

            var checkSum = ""
            if (file.isFile){
                val data = Files.readAllBytes(paths)
                val hash = MessageDigest.getInstance("MD5").digest(data)
                checkSum = BigInteger(1, hash).toString(16)
            }
            return FileItem(
                0,
                file.name,
                attributes.creationTime().toString().substring(0,10),
                file.length().toFloat()/1000,
                getFileIcon(file),
                file.absolutePath,
                checkSum
            )
        }

        //receives a file param and return an icon type by file extension
        private fun getFileIcon(file: File): Int {
            var type = 0
            if(file.isFile) {
                if(file.name.contains(".")) {
                    val extension = file.name.substring(file.name.lastIndexOf("."))
                    if (extension in listOf(".jpeg", ".jpg", ".png"))
                        type = R.drawable.icon_image
                    else if (extension in listOf(".txt", ".doc", ".docx"))
                        type = R.drawable.icon_doc
                    else if (extension.equals(".mp4"))
                        type = R.drawable.icon_video
                    else if (extension in listOf(".zip", ".rar"))
                        type = R.drawable.icon_zip
                    else if (extension.equals(".pdf"))
                        type = R.drawable.icon_pdf
                    else if (extension.equals(".apk"))
                        type = R.drawable.icon_apk
                    else if (extension.equals(".mp3"))
                        type = R.drawable.icon_music
                    else
                        type = R.drawable.icon_download
                }
            }
            else if (file.isDirectory)
                type = R.drawable.icon_folder
            return type
        }

        //returns the file mime type for the intent
        fun getMime(uri : Uri, context: Context) : String?{
            val cr = context.contentResolver
            return cr.getType(uri)
        }

    }
}