package com.amans.studenttaskmanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task:Task)

    @Query("Select * from tasks ORDER BY dueDate ASC")
    fun getTasks() : LiveData<List<Task>>

    @Query("Select * from tasks where priority = :priority")
    fun getTaskByPriority(priority: String): LiveData<List<Task>>
}