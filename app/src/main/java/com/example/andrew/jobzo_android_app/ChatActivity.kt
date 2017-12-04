package com.example.andrew.jobzo_android_app

import android.app.ActionBar
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.example.andrew.jobzo_android_app.models.Author
import com.example.andrew.jobzo_android_app.models.Message
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.*
import org.json.JSONArray
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
    private val user = Author("1")
    private val server = Author("2")
    private var imageLoader: ImageLoader? = null
    private var adapter: MessagesListAdapter<Message>? = null
    lateinit var toast: Toast
    lateinit var switch: ImageButton
    lateinit var jobs: FloatingActionButton
    lateinit var courses: FloatingActionButton
    lateinit var degrees: FloatingActionButton
    lateinit var jobsLayout: LinearLayout
    lateinit var coursesLayout: LinearLayout
    lateinit var degreesLayout: LinearLayout
    lateinit var showButton:Animation
    lateinit var hideButton:Animation
    lateinit var showLayout:Animation
    lateinit var hideLayout:Animation


    // showing toast message to user
    private fun showToast(text: String, length: Int){
        toast = Toast.makeText(applicationContext, text, length)
        toast.setGravity(Gravity.BOTTOM, 0, 50)
        toast.show()
    }

    // checking internet connection
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    // hiding Animation
    private fun hideButtons(){
        jobsLayout.visibility = View.GONE
        coursesLayout.visibility = View.GONE
        degreesLayout.visibility = View.GONE
        jobsLayout.startAnimation(hideLayout)
        coursesLayout.startAnimation(hideLayout)
        degreesLayout.startAnimation(hideLayout)
        switch.startAnimation(hideButton)
    }

    // creating activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        // setting the title of the chat activity
        val actionBar = supportActionBar
        actionBar!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar!!.setCustomView(R.layout.custom_actionbar)
        actionBar.show()

        // Get the widgets reference from XML layout
        switch = findViewById(R.id.switchButton) as ImageButton

        // Get a reference for the floating image buttons
        jobs = findViewById(R.id.jobs) as FloatingActionButton
        courses = findViewById(R.id.courses) as FloatingActionButton
        degrees = findViewById(R.id.degrees) as FloatingActionButton

        //Getting floating buttons linear layouts
        jobsLayout = findViewById(R.id.jobsView) as LinearLayout
        coursesLayout = findViewById(R.id.coursesView) as LinearLayout
        degreesLayout = findViewById(R.id.degreesView) as LinearLayout

        // referencing animations
        showButton = AnimationUtils.loadAnimation(this@ChatActivity, R.anim.show_button)
        hideButton = AnimationUtils.loadAnimation(this@ChatActivity, R.anim.hide_button)
        showLayout = AnimationUtils.loadAnimation(this@ChatActivity, R.anim.show_layout)
        hideLayout = AnimationUtils.loadAnimation(this@ChatActivity, R.anim.hide_layout)

        switch.setOnClickListener({
            if(jobsLayout.visibility == View.VISIBLE && coursesLayout.visibility == View.VISIBLE && degreesLayout.visibility == View.VISIBLE){
                hideButtons()
            }
            else {
                jobsLayout.visibility = View.VISIBLE
                coursesLayout.visibility = View.VISIBLE
                degreesLayout.visibility = View.VISIBLE
                jobsLayout.startAnimation(showLayout)
                coursesLayout.startAnimation(showLayout)
                degreesLayout.startAnimation(showLayout)
                switch.startAnimation(showButton)
            }
        })

        // Set a listener for floating jobs button
        jobs.setOnClickListener ({
            hideButtons()
            sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 1)
        })

        // Set a listener for floating courses button
        courses.setOnClickListener ({
            hideButtons()
            sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 2)
        })

        // Set a listener for floating degrees button
        degrees.setOnClickListener ({
            hideButtons()
            sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 3)
        })

        // creating preferences file
        prefs = this.getSharedPreferences("tokens", MODE_PRIVATE)

        // getting user inputView
        userInput = findViewById(R.id.input) as MessageInput

        // initializing the image loader
        imageLoader = ImageLoader { imageView, url -> Picasso.with(applicationContext).load(url).into(imageView) }

        // setting the adapter for the message list
        adapter = MessagesListAdapter<Message>("1", imageLoader)
        messagesList.setAdapter(adapter)

        // setting listeners
        userInput!!.setInputListener(MessageInput.InputListener {
            sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 0)
            true
        })

        // initializing the chat
        welcomeUser("https://radiant-basin-93715.herokuapp.com/welcome")
    }

    /*
    @param: url of welcome backend
    - make a user session for the user and send a welcome message
     */
    private fun welcomeUser(url: String) {
        // building request
        val request = Request.Builder()
                .url(url)
                .build()

        showToast("connecting to server...", 10)
        // sending request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed")
            }
            override fun onResponse(call: Call, response: Response) = try {

                toast.cancel()
                val responseBody = response.body()?.string()
                val body = JSONObject(responseBody)
                val respMessage = body.getString("message")

                // saving user id in shared preferences
                val prefsEditor = prefs!!.edit()
                val gson = Gson()
                val json = gson.toJson(body.get("uuid"))
                prefsEditor.putString("token", json)
                prefsEditor.commit()

                runOnUiThread {
                    adapter!!.addToStart(Message(respMessage, "1", server, null), true)
                }
            } catch (e: JSONException){
                println("JSON ERROR")
            }
        })
    }

    // shuffling method
    fun shuffleJsonArray(array: JSONArray): JSONArray {
        // Implementing Fisher–Yates shuffle
        val rnd = Random()
        for (i in array.length() - 1 downTo 0) {
            val j = rnd.nextInt(i + 1)
            // Simple swap
            val `object` = array.get(j)
            array.put(j, array.get(i))
            array.put(i, `object`)
        }
        return array
    }

    /*
    @param: url of chat backend
    - makes the user send his message and gets the server response
     */
    private fun sendMessage(url: String, type: Int) {
        // getting user id from shared preferences
        val gson = Gson()
        val json2 = prefs!!.getString("token", "")
        val userSession = gson.fromJson<String>(json2, String::class.java)

        // switching image buttons cases with the usual chat
        when (type) {
            0 -> {
                messageText = userInput!!.inputEditText.text.toString()
                adapter!!.addToStart(Message(messageText!!, "1", user, null), true)
            }
            1 -> {
                messageText = "jobs"
                adapter!!.addToStart(Message("jobs", "1", user, null), true)
            }
            2 -> {
                messageText = "courses"
                adapter!!.addToStart(Message("courses", "1", user, null), true)
            }
            3 -> {
                messageText = "degrees"
                adapter!!.addToStart(Message("degrees", "1", user, null), true)
            }
        }

        // building request body
        val body: HashMap<String, String> = hashMapOf("message" to "")
        body.put("message", messageText.toString())

        // building request
        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", userSession)
                .post(RequestBody.create(content, JSONObject(body).toString()))
                .build()

        // checking connectivity
        if (!isNetworkAvailable()) {
            showToast("please check your internet connection and try again", Toast.LENGTH_LONG)
        } else {
            showToast("Typing...", 10)
            // sending request
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException){
                    toast.cancel()
                    showToast("something went wrong please try again", Toast.LENGTH_LONG)
                }

                override fun onResponse(call: Call, response: Response) = try {
                    toast.cancel()
                    val responseBody = response.body()?.string()
                    val body = JSONObject(responseBody)
                            if (body.has("message")) {
                                val respMessage = body.getString("message")

                                runOnUiThread {
                                    if(response.code() == 400) {
                                        showToast("something went wrong, please try again", 50)
                                    }
                                    else if (response.code() == 401){
                                        showToast("Your session has expired, reloading...", 50)
                                        finish()
                                        startActivity(intent)
                                    }
                                    else {
                                        adapter!!.addToStart(Message(respMessage, "1", server, null), true)
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    // getting the items response array
                                    var items: JSONArray = body.getJSONArray("items")
                                    var link: String
                                    var title: String
                                    var pagemap: JSONObject
                                    var images: JSONArray? = null
                                    var url: String

                                    // shuffling result
                                    val result = shuffleJsonArray(items)

                                    // responding to user with the required search results
                                    var i = 0
                                    while (i < (result.length())) {
                                        pagemap = result.getJSONObject(i).getJSONObject("pagemap")
                                        try {
                                            images = pagemap!!.getJSONArray("cse_image")
                                        } catch (e: JSONException) {
                                            println("NULL")
                                        }

                                        if (images != null) {
                                            url = images.getJSONObject(0).getString("src")
                                            if (i == 0) {
                                                adapter!!.addToStart(Message("Image", "1", server, url), true)
                                            } else {
                                                adapter!!.addToStart(Message("Image", "1", server, url), false)
                                            }
                                        }

                                        title = result.getJSONObject(i).getString("title")
                                        link = result.getJSONObject(i).getString("link")
                                        var message = title + '\n' + link
                                        if (i == 0) {
                                            adapter!!.addToStart(Message(message, "1", server, null), true)
                                        } else {
                                            adapter!!.addToStart(Message(message, "1", server, null), false)
                                        }
                                        i++
                                    }
                                }
                            }
                } catch (e: JSONException) {
                    runOnUiThread {
                        showToast(e.message.toString() , Toast.LENGTH_LONG)
                    }
                    println(e.message)
                }
            })
        }
    }
}