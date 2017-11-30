package com.example.andrew.jobzo_android_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.graphics.drawable.ColorDrawable



class MainActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 4000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed(object:Runnable{
            public override fun run() {
                val home = Intent (this@MainActivity, ChatActivity::class.java)
                startActivity(home)
                finish()
            }
        },SPLASH_TIME_OUT.toLong())
        val actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.hide()

        setContentView(R.layout.activity_main)

    }
}
