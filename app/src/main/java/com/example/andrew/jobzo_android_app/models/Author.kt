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
        return avatar
    }
}
