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
            val category = defineCategory(it.id)
            navController?.navigate(R.id.action_mainFragment_to_fileFragment, bundleOf(Pair("category", category)))
        }

        val buttonsList = listOf(btnAllFiles, btnDownloads, btnPictures, btnMusic, btnMovies)
        buttonsList.forEach{it.setOnClickListener(onCategoryClick)}

        return view
    }

    //return a path for each category
    private fun defineCategory(id: Int): String {
        return when(id){
            R.id.btnAllFiles -> ""
            R.id.btnDownloads ->  "/download"
            R.id.btnPictures ->  "/pictures"
            R.id.btnMusic ->  "/music"
            R.id.btnMovies ->  "/movies"
            else -> {""}
        }
    }

    //updates the progress bar of used space
    private fun updateProgressBar() {
        val params = progressBar.layoutParams
        val dp = Resources.getSystem().displayMetrics.xdpi.roundToInt()
        val freeSpace = (Environment.getExternalStorageDirectory().freeSpace/1000000).toFloat()
        val totalSpace = (Environment.getExternalStorageDirectory().totalSpace/1000000).toFloat()
        params.width = ((dp*2)*(1-freeSpace/totalSpace)).toInt()
        progressBar.layoutParams = params
    }

    //inits all screen views
    private fun initViews(view : View){
        btnAllFiles = view.findViewById(R.id.btnAllFiles)
        btnDownloads = view.findViewById(R.id.btnDownloads)
        btnPictures = view.findViewById(R.id.btnPictures)
        btnMusic = view.findViewById(R.id.btnMusic)
        btnMovies = view.findViewById(R.id.btnMovies)
        recyclerView = view.findViewById(R.id.recyclerViewRecent)
        progressBar = view.findViewById<View>(R.id.progressBarSpace)
    }
}