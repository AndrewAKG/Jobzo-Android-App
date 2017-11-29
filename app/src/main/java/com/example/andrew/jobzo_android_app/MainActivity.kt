package com.example.andrew.jobzo_android_app

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException





class MainActivity : AppCompatActivity() {
    val CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8")
    private val client = OkHttpClient()
    private var prefs: SharedPreferences? = null
    private var message: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = this.getSharedPreferences("tokens", MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        val send= findViewById<ImageButton>(R.id.send)
        message = findViewById<EditText>(R.id.TextMessage)
       // val message= findViewById<EditText>(R.id.TextMessage)

        send.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
               sendMessage("https://radiant-basin-93715.herokuapp.com/chat")
            }
        })
       welcomeUser("https://radiant-basin-93715.herokuapp.com/welcome")
    }

    /*
    @params: url of welcome backend
    - make a user session for the user and send a welcome message
     */
    private fun welcomeUser(url: String) {
        // sending request
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                val body = JSONObject(responseBody)
                println(body.get("uuid"))

                // saving user id in local storage
                val prefsEditor = prefs!!.edit()
                val gson = Gson()
                val json = gson.toJson(body.get("uuid"))
                prefsEditor.putString("token", json)
                prefsEditor.commit()
            }
        })
    }

    /*
    @params: url of chat backend
    - makes the user send his message and gets the server response
     */
    fun sendMessage(url: String){
        // getting user session from local storage
        val gson = Gson()
        val json2 = prefs!!.getString("token", "")
        val userSession = gson.fromJson<String>(json2, String::class.java)

        // getting user message
        val messageText = message?.getText().toString()
        println(messageText)

        // building request body
        val body: HashMap<String, String> = hashMapOf("message" to "")
        body.put("message", messageText)

        // sending request
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
    }
}
