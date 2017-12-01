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
        return "https://odesk-prod-portraits.s3.amazonaws.com/Companies:3813315:CompanyLogoURL?AWSAccessKeyId=1XVAX3FNQZAFC9GJCFR2&Expires=2147483647&Signature=1uE9t9gBZZ8aepAyxOqtxcAK02Q%3D"
    }
}
