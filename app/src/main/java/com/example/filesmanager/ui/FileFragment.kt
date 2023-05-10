package com.example.filesmanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.filesmanager.FileHelper
import com.example.filesmanager.R
import com.example.filesmanager.model.FileItem
import com.example.filesmanager.viewmodels.InternalViewModel
import com.google.android.material.navigation.NavigationView
import java.io.File


class FileFragment : Fragment() {

    private lateinit var viewModel: InternalViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnDrawer: LinearLayout
    private lateinit var btnBack: LinearLayout
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_file, container, false)
        val category = arguments?.getString("category")
        initViews(view)
        viewModel = ViewModelProvider(this).get(InternalViewModel::class.java)
        category?.let { viewModel.setStartFrom(it) }

        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_host_fragment) }
        val adapter = FilesAdapter()

        viewModel.files.observe(
            viewLifecycleOwner,
            Observer {
                adapter.setFiles(it)
            }
        )

        //set the logic of pressing on the file
        adapter.setOnFileClickListener(object : FilesAdapter.OnFileClickListener {
            override fun onFileClick(fileParam: FileItem) {
                if(File(fileParam.absolutePath).isFile){
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val uri = FileProvider.getUriForFile(view.context, activity?.getApplicationContext()
                        ?.getPackageName() + ".provider", File(fileParam.absolutePath))
                    intent.setDataAndType(uri, FileHelper.getMime(uri, view.context))
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                } else {
                    viewModel.goIntoPath(fileParam.name)
                }
            }
        })

        recyclerView.adapter = adapter

        btnBack.setOnClickListener {
            if (viewModel.goBack())
                navController?.navigate(R.id.action_fileFragment_to_mainFragment)
        }

        btnDrawer.setOnClickListener{
            drawer.openDrawer(GravityCompat.END)
        }

        navigationView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.nav_radio_button1 -> setBehavior(item, "sizeDescending")
                    R.id.nav_radio_button2 -> setBehavior(item, "size")
                    R.id.nav_radio_button3 -> setBehavior(item, "dateDescending")
                    R.id.nav_radio_button4 -> setBehavior(item, "date")
                    R.id.chb_txt -> setBehavior(item, ".txt")
                    R.id.chb_png -> setBehavior(item, ".png")
                    R.id.chb_pdf -> setBehavior(item, ".pdf")
                    R.id.chb_mp3 -> setBehavior(item, ".mp3")
                }
                return true
            }
        })
        return view
    }

    //sets onClick logic for menu items
    fun setBehavior(item : MenuItem, sortType : String){
        item.setChecked(!item.isChecked)
        val newSortType = if (item.isChecked) sortType else ""
        viewModel.setTypeSort(newSortType)
    }

    //inits all screen views
    private fun initViews(view : View){
        recyclerView = view.findViewById(R.id.recyclerViewFiles)
        btnDrawer = view.findViewById(R.id.btnDrawer)
        btnBack = view.findViewById(R.id.btnBack)
        drawer = view.findViewById(R.id.drawer_layout)
        navigationView = view.findViewById(R.id.nav_view)
    }
}