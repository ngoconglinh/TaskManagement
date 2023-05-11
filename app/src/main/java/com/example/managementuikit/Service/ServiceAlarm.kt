package com.example.managementuikit.Service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.managementuikit.Broadcast.AlarmReceiver
import com.example.managementuikit.Model.Task
import java.util.*

class ServiceAlarm: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle: Bundle? = intent?.extras
        if (bundle!=null){
            @Suppress("DEPRECATION")
            val task = bundle.getSerializable("Task") as Task
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.starts_time
            val firingCal = Calendar.getInstance()
            firingCal[Calendar.YEAR] = calendar.get(Calendar.YEAR)
            firingCal[Calendar.MONTH] = calendar.get(Calendar.MONTH)
            firingCal[Calendar.DAY_OF_WEEK] = calendar.get(Calendar.DAY_OF_WEEK)
            firingCal[Calendar.HOUR_OF_DAY] = calendar.get(Calendar.HOUR_OF_DAY)
            firingCal[Calendar.MINUTE] =  calendar.get(Calendar.MINUTE)
            firingCal[Calendar.SECOND] = 0
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if (firingCal.timeInMillis >= Date().time){
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firingCal.timeInMillis,
                    createPendingIntent(task.title, task.status, this, task.id))
            }
        }
        return START_NOT_STICKY
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createPendingIntent(title: String, status: String, context: Context? , requestCode: Int): PendingIntent? {
        val notificationIntent = Intent(context, AlarmReceiver::class.java)
        notificationIntent.putExtra("title", title)
        notificationIntent.putExtra("status", status)
        notificationIntent.putExtra("id", requestCode)
        return PendingIntent.getBroadcast(context, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }






//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val bundle: Bundle? = intent?.extras
//        var listTask: ArrayList<Task> = ArrayList()
//        if (bundle!=null){
//            @Suppress("DEPRECATION")
//            listTask = bundle.getSerializable("listTask") as ArrayList<Task>
//        }
//        var count = 0
//        listTask.forEach {
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = it.starts_time
//
//            val firingCal = Calendar.getInstance()
//            firingCal[Calendar.HOUR_OF_DAY] = calendar.get(Calendar.HOUR_OF_DAY)
//            firingCal[Calendar.MINUTE] =  calendar.get(Calendar.MINUTE)
//            firingCal[Calendar.SECOND] = 0
//
//            val am = getSystemService(ALARM_SERVICE) as AlarmManager
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firingCal.timeInMillis,
//                createPendingIntent(it.title, it.status, this, count))
//            count++
//        }
//
//        return START_NOT_STICKY
//    }
//    @SuppressLint("UnspecifiedImmutableFlag")
//    fun createPendingIntent(title: String, status: String, context: Context? , requestCode: Int): PendingIntent? {
//        val notificationIntent = Intent(context, AlarmReceiver::class.java)
//        notificationIntent.putExtra("title", title)
//        notificationIntent.putExtra("status", status)
//        return PendingIntent.getBroadcast(context, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }

}