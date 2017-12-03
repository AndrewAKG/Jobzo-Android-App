package com.example.andrew.jobzo_android_app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 4000

    // checking internet connection
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    // alerting user if offline
    private fun showAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Please check your internet connection")
                .setCancelable(false)
                .setPositiveButton(
                        "Reload",
                        DialogInterface.OnClickListener { _ , _ ->
                            // reloading activity
                            finish();
                            startActivity(intent);
                        }
                )
        val alert = builder.create()
        alert.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isNetworkAvailable()){
            showAlertDialog()
        }
        else {
            Handler().postDelayed(object:Runnable{
                public override fun run() {
                    val home = Intent (this@MainActivity, ChatActivity::class.java)
                    startActivity(home)
                    finish()
                }
            },SPLASH_TIME_OUT.toLong())
        }
        val actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.hide()

        setContentView(R.layout.activity_main)
    }
}
