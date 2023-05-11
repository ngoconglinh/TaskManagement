package com.example.managementuikit.ViewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.managementuikit.DataBase.TaskRepository
import com.example.managementuikit.Model.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : ViewModel() {
    private val taskRepository: TaskRepository = TaskRepository(application)

    fun insertTask(task:Task) = viewModelScope.launch {
        taskRepository.insertTask(task)
    }
    fun updateTask(task:Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }
    fun deleteTask(task:Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }
    fun getAllTask():LiveData<List<Task>> = taskRepository.getAllTask()
    fun findTaskByStatus(status:String):LiveData<List<Task>> = taskRepository.findTaskByStatus(status)


    class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(TaskViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return TaskViewModel(application) as T
            }
            throw java.lang.IllegalArgumentException("Unable construct viewModel")
        }
    }
}
