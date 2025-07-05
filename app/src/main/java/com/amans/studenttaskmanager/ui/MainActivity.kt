package com.amans.studenttaskmanager.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amans.studenttaskmanager.R
import com.amans.studenttaskmanager.ReminderReceiver
import com.amans.studenttaskmanager.data.Task
import com.amans.studenttaskmanager.databinding.ActivityMainBinding
import com.amans.studenttaskmanager.viewmodel.TaskViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = TaskAdapter()
        binding.rvView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvView.adapter = adapter
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskViewModel.allTasks.observe(this) { tasks ->
            adapter.submitList(tasks)
        }

        val fSpinner = binding.filterSpinner
        val sAdapter = ArrayAdapter.createFromResource(
            this, R.array.filter_options, android.R.layout.simple_spinner_item
        )
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fSpinner.adapter = sAdapter

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.getTaskAt(position)
                taskViewModel.delete(task)
                Toast.makeText(this@MainActivity, "Task Deleted", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvView)

        adapter.onItemClick = { task ->
            val dialog = AddTaskDialogFragment.newInstance(task)
            dialog.show(supportFragmentManager, "EditTaskDialog")
        }

        adapter.onReminderToggle = { task ->
            taskViewModel.update(task)

            if (task.hasReminder) {
                scheduleReminder(this, task)
                Toast.makeText(this, "Reminder set", Toast.LENGTH_SHORT).show()
            } else {
                cancelReminder(this, task)
                Toast.makeText(this, "Reminder removed", Toast.LENGTH_SHORT).show()
            }
        }

        adapter.onCompleteToggle = { updateTask ->
            taskViewModel.update(updateTask)
        }

        val dialog = AddTaskDialogFragment()
        dialog.isCancelable = false
        binding.addTaskFAB.setOnClickListener {
            dialog.show(supportFragmentManager, "AddTaskDialog")
        }

        taskViewModel.allTasks.observe(this) { allTasks ->
            binding.filterSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selected = parent?.getItemAtPosition(position).toString()
                        val filtered = taskViewModel.filterTasks(selected)
                        adapter.submitList(filtered)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    fun cancelReminder(context: Context, task: Task){
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleReminder(context: Context, task: Task){
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", task.title)
            putExtra("description", task.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, task.id,intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            task.timeInMili,
            pendingIntent
        )
    }
}