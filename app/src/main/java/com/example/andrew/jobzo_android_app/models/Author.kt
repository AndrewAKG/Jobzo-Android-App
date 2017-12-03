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
        return "https://scontent-cai1-1.xx.fbcdn.net/v/t34.0-12/24337234_1716463408397484_1147792364_n.png?oh=bcc66cf9e74b2a1ffcc2c688b015c207&oe=5A26116A"
        //"https://odesk-prod-portraits.s3.amazonaws.com/Companies:3813315:CompanyLogoURL?AWSAccessKeyId=1XVAX3FNQZAFC9GJCFR2&Expires=2147483647&Signature=1uE9t9gBZZ8aepAyxOqtxcAK02Q%3D"
    }
}
