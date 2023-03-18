package com.ndroid.weatherapp2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
    }
    fun onClick(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}