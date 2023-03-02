package com.example.simpleksp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.ksp.Hacked
import generated.file.printHackFunction

class MainActivity : AppCompatActivity() {
    @Hacked()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.my_text).text = printHackFunction()
    }

    @Hacked
    private fun internalFunction() = {}
}

@Hacked
fun externalFunction() = {}
