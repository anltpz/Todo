package com.example.todoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.todoapp.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupActionBarWithNavController(findNavController(R.id.navHostFragment))

    }

    override fun onSupportNavigateUp(): Boolean {
        val navContoller = findNavController(R.id.navHostFragment)
        return navContoller.navigateUp() || super.onSupportNavigateUp()
    }
}