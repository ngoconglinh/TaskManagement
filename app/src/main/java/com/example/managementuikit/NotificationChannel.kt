package com.example.managementuikit

import android.app.Application
import android.app.NotificationManager
import android.app.NotificationChannel

class NotificationChannel: Application() {
    companion object{
        const val CHANNEL_ID = "notification"
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val mChannel = NotificationChannel(CHANNEL_ID, "notification", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager:NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

}