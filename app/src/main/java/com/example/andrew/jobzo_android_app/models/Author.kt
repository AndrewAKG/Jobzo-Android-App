package com.example.andrew.jobzo_android_app.models

import com.stfalcon.chatkit.commons.models.IUser

class Author(private val userId: String) : IUser {

    override fun getId(): String? {
        return userId
    }

    override fun getName(): String {
        return name
    }

    override fun getAvatar(): String {
        if (userId == "2") {
            return "https://radiant-basin-93715.herokuapp.com/image"
        }
        return ""
    }
}
