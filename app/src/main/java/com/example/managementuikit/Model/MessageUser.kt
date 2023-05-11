package com.example.managementuikit.Model

import java.io.Serializable
data class MessageUser(
    val date: String,
    val sender:String,
    val text:String
) : Serializable