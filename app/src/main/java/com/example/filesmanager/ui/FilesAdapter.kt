package com.example.filesmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.filesmanager.R
import com.example.filesmanager.model.FileItem

class FilesAdapter : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    private lateinit var files : List<FileItem>
    private lateinit var onFileClickListener : OnFileClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)

        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files.get(position)
        holder.tvName.text = file.name
        holder.tvDate.text = file.date
        holder.tvSize.text = getFileSize(file.size)

        if(file.fileIcon!=0)
            holder.fileIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, file.fileIcon))

        holder.cardItem.setOnClickListener{
            onFileClickListener.onFileClick(file)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    //convert the file size in bytes to mB or gB
    fun getFileSize(size: Float) : String{
        var newSize = ""
        if(size < 1000)
            newSize = "${size.toInt()} kB"
        else if (size >=1000)
            newSize = "${(size/1000).toInt()} mB"
        else
            newSize = "${(size/1000000).toInt()} gB"

        return newSize
    }

    fun setFiles(files : List<FileItem>){
        this.files = files
        notifyDataSetChanged()
    }

    fun setOnFileClickListener(onFileClickListener : OnFileClickListener){
        this.onFileClickListener = onFileClickListener
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName : TextView = itemView.findViewById(R.id.tvFile)
        var cardItem : CardView = itemView.findViewById(R.id.card_item)
        var tvDate : TextView = itemView.findViewById(R.id.tvDate)
        var tvSize : TextView = itemView.findViewById(R.id.tvSize)
        var fileIcon : ImageView = itemView.findViewById(R.id.fileIcon)
    }

    interface OnFileClickListener{
        fun onFileClick(file : FileItem)
    }
}