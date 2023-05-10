package com.example.filesmanager.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.filesmanager.R
import com.example.filesmanager.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //request the user's permission
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1)
        //request the user's permission
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1);

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.insertAllFilesHash()
    }
}