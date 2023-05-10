package com.example.filesmanager.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.filesmanager.FileHelper
import com.example.filesmanager.R
import com.example.filesmanager.RecentFilesStore
import com.example.filesmanager.model.FileItem
import com.example.filesmanager.viewmodels.MainViewModel
import java.io.File
import kotlin.math.roundToInt


class MainFragment : Fragment() {

    private lateinit var btnAllFiles : CardView
    private lateinit var btnDownloads : CardView
    private lateinit var btnPictures : CardView
    private lateinit var btnMusic : CardView
    private lateinit var btnMovies : CardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View

    private lateinit var viewModel : MainViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        initViews(view)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.checkRecentFiles()

        updateProgressBar()

        val adapter = FilesAdapter()

        adapter.setFiles(RecentFilesStore.getRecentFiles())
        adapter.setFiles(arrayListOf(FileItem(0, "", "", 1f, 0, "", "")))
        recyclerView.visibility = View.GONE
        viewModel.recent.observe(
            viewLifecycleOwner,
            Observer {
                RecentFilesStore.setRecentFiles(it)
                adapter.setFiles(RecentFilesStore.getRecentFiles())
                recyclerView.visibility = View.VISIBLE
            }
        )

        //set the logic of pressing on the file
        adapter.setOnFileClickListener(object : FilesAdapter.OnFileClickListener {
            override fun onFileClick(fileParam: FileItem) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                val uri = FileProvider.getUriForFile(view.context, activity?.getApplicationContext()
                    ?.getPackageName() + ".provider", File(fileParam.absolutePath)
                )
                intent.setDataAndType(uri, FileHelper.getMime(uri, view.context))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
        })

        recyclerView.adapter = adapter

        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_host_fragment) }

        val onCategoryClick = View.OnClickListener {
            var category = ""
            when(it.id){
                R.id.btnAllFiles -> category = ""
                R.id.btnDownloads -> category = "/download"
                R.id.btnPictures -> category = "/pictures"
                R.id.btnMusic -> category = "/music"
                R.id.btnMovies -> category = "/movies"
            }
            navController?.navigate(R.id.action_mainFragment_to_fileFragment, bundleOf(Pair("category", category)))
        }

        btnAllFiles.setOnClickListener(onCategoryClick)
        btnDownloads.setOnClickListener(onCategoryClick)
        btnPictures.setOnClickListener(onCategoryClick)
        btnMusic.setOnClickListener(onCategoryClick)
        btnMovies.setOnClickListener(onCategoryClick)

        return view
    }

    //updates the progress bar of used space
    private fun updateProgressBar() {
        val params = progressBar.layoutParams
        val dp = Resources.getSystem().displayMetrics.xdpi.roundToInt()
        val a = (Environment.getExternalStorageDirectory().freeSpace/1000000).toFloat()
        val b = (Environment.getExternalStorageDirectory().totalSpace/1000000).toFloat()
        params.width = ((dp*2)*(1-a/b)).toInt()
        progressBar.layoutParams = params
    }

    //inits all screen views
    fun initViews(view : View){
        btnAllFiles = view.findViewById(R.id.btnAllFiles)
        btnDownloads = view.findViewById(R.id.btnDownloads)
        btnPictures = view.findViewById(R.id.btnPictures)
        btnMusic = view.findViewById(R.id.btnMusic)
        btnMovies = view.findViewById(R.id.btnMovies)
        recyclerView = view.findViewById(R.id.recyclerViewRecent)
        progressBar = view.findViewById<View>(R.id.progressBarSpace)
    }
}