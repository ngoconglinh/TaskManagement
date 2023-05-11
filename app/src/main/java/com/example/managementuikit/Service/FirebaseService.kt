package com.example.managementuikit.Service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.managementuikit.Activity.HomeActivity
import com.example.managementuikit.NotificationChannel
import com.example.managementuikit.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService(){
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification!=null){
            createNotification(message.notification!!.title!!, message.notification!!.body!!, message.data["id"]!!.toInt())
        }
    }
    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    private fun createNotification(title: String, status: String, id : Int) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("Task_ID", id)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val builder = NotificationCompat.Builder(this, NotificationChannel.CHANNEL_ID)
            .setSmallIcon(R.drawable.cancel_icon)
            .setContentTitle(title)
            .setContentText(status)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,builder)
    }


}