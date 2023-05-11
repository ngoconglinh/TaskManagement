package com.example.managementuikit.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.managementuikit.Model.Task

@Database(entities = [Task::class], version = 3)
abstract class TaskDataBase: RoomDatabase() {
    abstract fun getTaskDao():TaskDao

    companion object{
        @Volatile
        private var instance:TaskDataBase?=null
        fun getInstance(context: Context): TaskDataBase{
            if (instance == null){
                instance = Room.databaseBuilder(context, TaskDataBase::class.java, "TaskDatabase").build()
            }
            return instance!!
        }
    }
}