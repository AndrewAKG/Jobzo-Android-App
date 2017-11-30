package com.example.andrew.jobzo_android_app

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.andrew.jobzo_android_app.models.Author
import com.example.andrew.jobzo_android_app.models.Message
import com.google.gson.Gson
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class ChatActivity : AppCompatActivity() {
    private val content = MediaType.parse("application/json; charset=utf-8")
    private val client = OkHttpClient()
    private var prefs: SharedPreferences? = null
    private var userInput: MessageInput? = null
    private var messageText: String? = null
    private val adapter = MessagesListAdapter<Message>("1", null)
    private val user = Author("1")
    private val server = Author("2")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Jobzo")
        setContentView(R.layout.activity_chat)
        prefs = this.getSharedPreferences("tokens", MODE_PRIVATE)
        userInput = findViewById(R.id.input) as MessageInput
        messagesList.setAdapter(adapter)
        val coursesMsg= findViewById(R.id.courses)
        val jobsMsg= findViewById(R.id.jobs)
        val degreesMsg= findViewById(R.id.degrees)

        userInput!!.setInputListener(MessageInput.InputListener {
            sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 0)
            true
        })
        jobsMsg.setOnClickListener { sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 1) }
        coursesMsg.setOnClickListener { sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 2) }
        degreesMsg.setOnClickListener { sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 3) }

        // initializing the chat
        welcomeUser("https://radiant-basin-93715.herokuapp.com/welcome")
    }

    /*
    @param: url of welcome backend
    - make a user session for the user and send a welcome message
     */
    private fun welcomeUser(url: String) {
        // sending request
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed")
            }
            override fun onResponse(call: Call, response: Response) = try {
                val responseBody = response.body()?.string()
                val body = JSONObject(responseBody)
                val respMessage = body.getString("message")

                // saving user id in local storage
                val prefsEditor = prefs!!.edit()
                val gson = Gson()
                val json = gson.toJson(body.get("uuid"))
                prefsEditor.putString("token", json)
                prefsEditor.commit()

                runOnUiThread {
                    adapter.addToStart(Message(respMessage, "1", server), true);
                }
            } catch (e: JSONException){
                println("JSON ERROR")
            }
        })
    }

    /*
    @param: url of chat backend
    - makes the user send his message and gets the server response
     */
    private fun sendMessage(url: String, type: Int){
        // getting user session from local storage
        val gson = Gson()
        val json2 = prefs!!.getString("token", "")
        val userSession = gson.fromJson<String>(json2, String::class.java)

        when(type){
            0 -> {
                messageText = userInput!!.inputEditText.text.toString()
                adapter.addToStart(Message(messageText!!, "1", user), true);
            }
            1 -> {
                messageText = "jobs"
                adapter.addToStart(Message("jobs", "1", user), true);
            }
            2 -> {
                messageText = "courses"
                adapter.addToStart(Message("courses", "1", user), true);
            }
            3 -> {
                messageText = "degrees"
                adapter.addToStart(Message("degrees", "1", user), true);
            }
        }

        // building request body
        val body: HashMap<String, String> = hashMapOf("message" to "")
        body.put("message", messageText.toString())

        // sending request
        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization",userSession)
                .post(RequestBody.create(content, JSONObject(body).toString()))
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed")
            }
            override fun onResponse(call: Call, response: Response) = try {
                val responseBody = response.body()?.string()
                val body = JSONObject(responseBody)
                if (body.has("message")) {
                    println(body.getString("message"))
                    val respMessage = body.getString("message")
                    runOnUiThread {
                        adapter.addToStart(Message(respMessage, "1", server), true);
                    }
                } else {
                    println(body)
                }
            } catch (e: JSONException){
                println("ERROR")
            }
        })
    }
}
