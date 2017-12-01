package com.example.andrew.jobzo_android_app.models

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

class Message(private val message: String, private val msgId: String = "1", val author: Author, val url: String? = null) : IMessage, MessageContentType.Image {

    override fun getId(): String? {
        return msgId
    }

    override fun getText(): String {
        return message
    }

    override fun getUser(): Author {
        return author
    }

    override fun getCreatedAt(): Date {
        return Date()
    }

    override fun getImageUrl(): String? {
        return url
    }
}
