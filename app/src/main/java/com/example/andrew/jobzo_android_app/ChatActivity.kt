package com.example.andrew.jobzo_android_app

import android.app.ActionBar
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
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
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.app.Activity
import android.widget.Button
import android.widget.ImageButton
import android.support.v4.view.ViewCompat.setElevation
import android.os.Build
import android.view.LayoutInflater
import android.view.View


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
    private var mContext: Context? = null
    private var mActivity: Activity? = null

    private var mRelativeLayout: RelativeLayout? = null
    private var mButton: ImageButton? = null

    private var mPopupWindow: PopupWindow? = null


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

    // creating activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mContext = applicationContext

        // Get the activity
        mActivity = this@ChatActivity

        // Get the widgets reference from XML layout
        mRelativeLayout = findViewById(R.id.chat) as RelativeLayout
        mButton = findViewById(R.id.popupButton) as ImageButton

        // Set a click listener for the text view
        mButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                // Initialize a new instance of LayoutInflater service
                val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                // Inflate the custom layout/view
                val customView = inflater.inflate(R.layout.custom_layout, null)
                mPopupWindow = PopupWindow(
                        customView,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT
                )

                // Set an elevation value for popup window
                // Call requires API level 21
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow!!.setElevation(5.0f)
                }

                // Get a reference for the custom view close button
                val courses = customView.findViewById<ImageButton>(R.id.courses) as ImageButton
                val degrees = customView.findViewById<ImageButton>(R.id.degrees) as ImageButton
                val jobs = customView.findViewById<ImageButton>(R.id.jobs) as ImageButton

                // Set a click listener for the popup window jobs button
                jobs.setOnClickListener({
                        mPopupWindow!!.dismiss()
                        sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 1)
                })

                // Set a click listener for the popup window courses button
                courses.setOnClickListener({
                        mPopupWindow!!.dismiss()
                     sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 2)
                })
                // Set a click listener for the popup window degrees button
                degrees.setOnClickListener({
                        mPopupWindow!!.dismiss()
                        sendMessage("https://radiant-basin-93715.herokuapp.com/chat", 3)
                })

                mPopupWindow!!.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0)
            }
        })
        // setting the title of the chat activity
        val actionBar = supportActionBar
        actionBar!!.title = "Jobzo"
        actionBar.show()

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
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed")
                }

                override fun onResponse(call: Call, response: Response) = try {
                    toast.cancel()
                    val responseBody = response.body()?.string()
                    val body = JSONObject(responseBody)
                    if (body.has("message")) {
                        val respMessage = body.getString("message")
                        runOnUiThread {
                            adapter!!.addToStart(Message(respMessage, "1", server, null), true)
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
                                    adapter!!.addToStart(Message("Image", "1", server, url), false)
                                }

                                title = result.getJSONObject(i).getString("title")
                                link = result.getJSONObject(i).getString("link")
                                var message = title + '\n' + link
                                adapter!!.addToStart(Message(message, "1", server, null), false)
                                i++
                            }
                        }
                    }
                } catch (e: JSONException) {
                    println(e)
                }
            })
        }
    }
}
