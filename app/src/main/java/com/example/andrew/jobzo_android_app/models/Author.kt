package com.example.andrew.jobzo_android_app.models

import com.stfalcon.chatkit.commons.models.IUser


class Author : IUser {
    internal var id: String? = null

    override fun getId(): String? {
        return id
    }

    override fun getName(): String {
        return name
    }

    override fun getAvatar(): String {
        return avatar
    }
}
