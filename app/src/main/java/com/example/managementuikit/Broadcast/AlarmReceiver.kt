package com.example.managementuikit.Broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.HashMap

class AlarmReceiver: BroadcastReceiver() {
    private lateinit var requestQueue: RequestQueue
    private val fcmAPI = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAAhcwMTI:APA91bGdL4HokvFR2B37Gb_u4LtYWZY2lxmSa6JIcfhxOZGH_R00JZfDyKFNQvpS7SyvK0gGE-AaOSPYM-JHx8wNIKDtsCZ33qPplpr08GM84lhNEh5NM7tRqlsbaVivS7MK6fsmaaXD"
    private val contentType = "application/json"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        requestQueue = Volley.newRequestQueue(context)
        val extras = intent.extras
        val title = extras?.getString("title")
        val status = extras?.getString("status")
        val id = extras?.getInt("id")
        setDataNotification(title.toString(), status.toString(), id.toString())
    }

    private fun setDataNotification(title: String, body: String, id :String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val topic = task.result
            val notification = JSONObject()
            notification.put("to", topic)
            val notificationBody = JSONObject()
            notificationBody.put("title", title)
            notificationBody.put("body", body)
            notificationBody.put("click_action", "open_HomeActivity")
            notification.put("notification", notificationBody)
            val notificationData = JSONObject()
            notificationData.put("id", id)
            notification.put("data", notificationData)

            sendNotification(notification)
        })
    }

    @Suppress("DEPRECATION")
    private fun sendNotification(notification: JSONObject) {
        val jsonObjectRequest = object : JsonObjectRequest(fcmAPI, notification,
            Response.Listener { response ->
                Log.d("setDataNotification", "onResponse: $response")
            },
            Response.ErrorListener {
                Log.d("setDataNotification", "onErrorResponse: Didn't work")
            })
        {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }



//    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
//    private fun createNotification(context: Context, title: String, status: String) {
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, Intent(context, HomeActivity::class.java),
//            PendingIntent.FLAG_ONE_SHOT)
//        val builder = NotificationCompat.Builder(context, NotificationChannel.CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle(title)
//            .setContentText(status)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(context)) { notify(1, builder.build())}
//    }
}