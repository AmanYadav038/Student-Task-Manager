package com.amans.studenttaskmanager.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.amans.studenttaskmanager.ReminderReceiver
import com.amans.studenttaskmanager.data.Task
import com.amans.studenttaskmanager.data.TaskDao
import com.amans.studenttaskmanager.data.TaskDatabase
import com.amans.studenttaskmanager.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : TaskRepository
    val allTasks : LiveData<List<Task>>

    init {
        val dao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        allTasks = repository.allTask
    }

    fun insert(task : Task) = viewModelScope.launch { repository.insert(task) }
    fun update(task : Task) = viewModelScope.launch { repository.update(task) }
    fun delete(task : Task) = viewModelScope.launch { repository.delete(task) }
    fun filterTasks(option: String): List<Task> {
        return when(option) {
            "High Priority" -> allTasks.value?.filter { it.priority == "High" } ?: emptyList()
            "Medium Priority" -> allTasks.value?.filter { it.priority == "Medium" } ?: emptyList()
            "Low Priority" -> allTasks.value?.filter { it.priority == "Low" } ?: emptyList()
            "Completed" -> allTasks.value?.filter { it.isCompleted } ?: emptyList()
            "Pending" -> allTasks.value?.filter { !it.isCompleted } ?: emptyList()
            else -> allTasks.value ?: emptyList()
        }
    }
}