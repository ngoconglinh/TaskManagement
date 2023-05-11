package com.example.managementuikit.DataBase

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.managementuikit.Model.Task

class TaskRepository(application: Application) {
    private val taskDao:TaskDao

    init {
        val taskDataBase:TaskDataBase = TaskDataBase.getInstance(application)
        taskDao = taskDataBase.getTaskDao()
    }
    suspend fun insertTask(task:Task) = taskDao.insertTask(task)
    suspend fun updateTask(task:Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task:Task) = taskDao.deleteTask(task)
    fun getAllTask():LiveData<List<Task>> = taskDao.getAllTask()
    fun findTaskByStatus(status:String):LiveData<List<Task>> = taskDao.findTaskByStatus(status)
}