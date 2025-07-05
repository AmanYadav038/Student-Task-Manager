package com.amans.studenttaskmanager.repository

import com.amans.studenttaskmanager.data.Task
import com.amans.studenttaskmanager.data.TaskDao

class TaskRepository(private val dao : TaskDao) {
    val allTask = dao.getTasks()
    fun getTaskByPriority(priority : String) = dao.getTaskByPriority(priority)

    suspend fun insert(task: Task)= dao.insertTask(task)
    suspend fun update(task: Task) = dao.updateTask(task)
    suspend fun delete(task: Task) = dao.deleteTask(task)
}