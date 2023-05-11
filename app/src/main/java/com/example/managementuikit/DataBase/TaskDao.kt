package com.example.managementuikit.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.managementuikit.Model.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task:Task)
    @Update
    suspend fun updateTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)
    @Query("SELECT * FROM task_table")
    fun getAllTask():LiveData<List<Task>>
    @Query("SELECT * FROM task_table")
    fun getAllTaskNonLive():List<Task>
    @Query("SELECT * FROM task_table WHERE Status_col=:status")
    fun findTaskByStatus(status:String):LiveData<List<Task>>
}