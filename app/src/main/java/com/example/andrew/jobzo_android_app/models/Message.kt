package com.example.andrew.jobzo_android_app.models

import com.stfalcon.chatkit.commons.models.IMessage
import java.util.*

class Message : IMessage {
    internal var id: String? = null
    internal var text: String? = null
    internal var author: Author? = null
    internal var createdAt: Date? = null

    /*...*/

    override fun getId(): String? {
        return id
    }

    override fun getText(): String? {
        return text
    }

    override fun getUser(): Author? {
        return author
    }

    override fun getCreatedAt(): Date? {
        return createdAt
    }
}
