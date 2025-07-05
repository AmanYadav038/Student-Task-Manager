package com.amans.studenttaskmanager.ui

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amans.studenttaskmanager.R
import com.amans.studenttaskmanager.data.Task
import com.amans.studenttaskmanager.databinding.TaskItemBinding

class TaskAdapter() : RecyclerView.Adapter<TaskAdapter.VH>(){

    private val taskList = mutableListOf<Task>()

    var onReminderToggle: ((Task)-> Unit)? = null

    var onItemClick : ((Task)->Unit)? = null

    var onCompleteToggle : ((Task)-> Unit)? = null

    fun submitList(list : List<Task>){
        taskList.clear()
        taskList.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onItemClick?.invoke(taskList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = TaskItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun getTaskAt(position: Int): Task{
        return taskList[position]
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = taskList[position]
        holder.binding.tvTaskTitle.text = task.title
        holder.binding.tvDueDate.text = task.dueDate
        holder.binding.textView.text = task.description
        holder.binding.tvPriority.text = task.priority
        when(task.priority){
            "High" -> holder.binding.tvPriority.setBackgroundResource(R.drawable.bg_priority_red)
            "Medium" -> holder.binding.tvPriority.setBackgroundResource(R.drawable.bg_priority_yellow)
            "Low" -> holder.binding.tvPriority.setBackgroundResource(R.drawable.bg_priority_green)
        }
        holder.binding.imgReminderToggle.setImageResource(
            if(task.hasReminder) R.drawable.ic_alarm_on else R.drawable.ic_alarm_off
        )

        holder.binding.imgReminderToggle.setOnClickListener{
            val updateTask = task.copy(hasReminder =  !task.hasReminder)
            onReminderToggle?.invoke(updateTask)
        }

        holder.binding.cbTaskStatus.isChecked = task.isCompleted

        if(task.isCompleted){
            holder.binding.tvTaskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvTaskTitle.setTextColor(Color.GRAY)
        }else{
            holder.binding.tvTaskTitle.paintFlags = 0
            holder.binding.tvTaskTitle.setTextColor(Color.BLACK)
        }

        holder.binding.cbTaskStatus.setOnCheckedChangeListener{_, isChecked->
            val updateTask = task.copy(isCompleted = isChecked)
            onCompleteToggle?.invoke(updateTask)

        }
    }
}