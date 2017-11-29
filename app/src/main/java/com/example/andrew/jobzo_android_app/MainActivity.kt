package com.example.andrew.jobzo_android_app

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import org.json.JSONException
import android.R.string.cancel
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import okhttp3.RequestBody
import okhttp3.OkHttpClient
import com.example.andrew.jobzo_android_app.R.id.send





class MainActivity : AppCompatActivity() {
    val CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8")
    val client = OkHttpClient()
    var prefs: SharedPreferences? = null
    var message: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = this.getSharedPreferences("tokens", MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        val send= findViewById<ImageButton>(R.id.send)
        message = findViewById<EditText>(R.id.TextMessage)
       // val message= findViewById<EditText>(R.id.TextMessage)

        send.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
               run2("https://radiant-basin-93715.herokuapp.com/chat")
            }
        })
       run("https://radiant-basin-93715.herokuapp.com/welcome")
    }


    fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                val body = JSONObject(responseBody)
                println(body.get("uuid"))
                val prefsEditor = prefs!!.edit()
                val gson = Gson()
                val json = gson.toJson(body.get("uuid"))
                prefsEditor.putString("token", json)
                prefsEditor.commit()

                val json2 = prefs!!.getString("token", "")
                val obj = gson.fromJson<String>(json2, String::class.java)
                println(obj)
            }
        })
    }
    fun run2(url: String){

        val gson = Gson()
        val json2 = prefs!!.getString("token", "")
        val userSession = gson.fromJson<String>(json2, String::class.java)
        val messageText = message?.getText().toString()
        println(messageText)

        val body: HashMap<String, String> = hashMapOf("message" to "")
        body.put("message", messageText)

        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization",userSession)
                .post(RequestBody.create(CONTENT_TYPE, JSONObject(body).toString()))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                val body = JSONObject(responseBody)
               if(body.has("message")){
                    println(body.getString("message"))
                }else{
                   println(body)
               }
            }

        })

//        val body = HashMap()
//        body.put("email", email)
//        val client = OkHttpClient()
//        val request = Request.Builder()
//                .url("http://ieeeguc.org/api/forgotPassword")
//                .post(RequestBody.create(CONTENT_TYPE, JSONObject(body).toString()))
//                .build()
//        client.newCall(request).enqueue(object : Callback() {
//            fun onFailure(call: Call, e: IOException) {
//                HTTP_RESPONSE.onFailure(-1, null)
//                call.cancel()
//            }
//
//            @Throws(IOException::class)
//            fun onResponse(call: Call, response: Response) {
//                try {
//                    val body = response.body().string()
//                    HTTP_RESPONSE.onSuccess(200, JSONObject(body))
//                } catch (e: JSONException) {
//                    HTTP_RESPONSE.onFailure(500, null)
//                }
//
//                response.close()
//            }
//        })
    }
}
