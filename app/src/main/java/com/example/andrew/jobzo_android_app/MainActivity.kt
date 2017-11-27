package com.example.andrew.jobzo_android_app

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val client = OkHttpClient()
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = this.getSharedPreferences("tokens", MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        run("https://radiant-basin-93715.herokuapp.com/welcome")
    }

    fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response){
                val responseBody = response.body()?.string()
                val body = JSONObject(responseBody)
                println(body.get("uuid"))
                val prefsEditor = prefs!!.edit()
                val gson = Gson()
                val json = gson.toJson(body.get("uuid"))
                prefsEditor.putString("token", json)
                prefsEditor.commit()

                val json2 = prefs!!.getString("token" , "")
                val obj = gson.fromJson<String>(json2,String::class.java)
                println(obj)
            }
        })
    }
}
