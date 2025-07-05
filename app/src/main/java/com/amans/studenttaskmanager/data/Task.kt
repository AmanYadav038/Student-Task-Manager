package com.amans.studenttaskmanager.data

import android.webkit.WebSettings.RenderPriority
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title : String,
    val description : String,
    val priority: String,
    val category : String,
    val dueDate : String,
    val timeInMili : Long,
    val hasReminder: Boolean = false,
    val isCompleted : Boolean = false
) : Serializable