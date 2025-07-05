package com.amans.studenttaskmanager.ui

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.amans.studenttaskmanager.R
import com.amans.studenttaskmanager.data.Task
import com.amans.studenttaskmanager.databinding.DialogAddTaskBinding
import com.amans.studenttaskmanager.viewmodel.TaskViewModel
import com.amans.studenttaskmanager.viewmodel.TaskViewModelFactory
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTaskDialogFragment : DialogFragment() {
    private val viewModel : TaskViewModel by activityViewModels{
        TaskViewModelFactory(requireActivity().application)
    }

    private var taskToEdit : Task? = null
    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!

    companion object{
        private const val ARG_TASK = "task_arg"

        fun newInstance(task: Task?) : AddTaskDialogFragment{
            val fragment = AddTaskDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_TASK, task as java.io.Serializable)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskToEdit = arguments?.getSerializable(ARG_TASK) as? Task
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        var dueDateValue = ""
        var timeInMili = 0L
        binding.pickerBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(), // or requireContext() if in Fragment
                { _, year, month, day ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, day)
                    }
                    timeInMili = selectedDate.timeInMillis
                    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                        Date(timeInMili)
                    )
                    binding.timePicker.setText(formattedDate)
                    dueDateValue = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        //set up spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter

        binding.addTaskButton.setOnClickListener {
            val taskTitle = binding.taskName.text.toString()
            val desc = binding.taskDesc.text.toString()
            val priority = binding.spinnerPriority.selectedItem.toString()
            val dueDate = dueDateValue
            // call a callback or ViewModel to save
            if(taskTitle.isNotEmpty()) {
                val task = Task(
                    id = taskToEdit?.id ?: 0,
                    title = taskTitle,
                    description = desc,
                    priority = priority,
                    dueDate = dueDate,
                    timeInMili = timeInMili,
                    category = "General"
                )
                if(taskToEdit == null){
                    viewModel.insert(task)
                }else{
                    viewModel.update(task)
                }
                dismiss()
            }else{
                Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        taskToEdit?.let { task->
            binding.taskName.setText(task.title)
            binding.taskDesc.setText(task.description)

            binding.timePicker.setText(task.dueDate)

            val spinnerIndex = adapter.getPosition(task.priority)
            binding.spinnerPriority.setSelection(spinnerIndex)

            binding.addTaskButton.text = "Update Task"
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        return dialog
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
