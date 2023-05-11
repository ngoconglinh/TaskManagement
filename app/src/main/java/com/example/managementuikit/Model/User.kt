package com.example.managementuikit.Model

import java.io.Serializable
data class User(
    val avatar :String,
    val userID:String,
    val name: String
) : Serializable