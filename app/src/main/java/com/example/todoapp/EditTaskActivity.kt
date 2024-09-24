package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.databinding.ActivityEditTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private var taskId: Long = -1
    private lateinit var calendar: Calendar
    private var isDateSelected = false
    private var isTimeSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        calendar = Calendar.getInstance()

        taskId = intent.getLongExtra("TASK_ID", -1)

        if (taskId != -1L) {
            loadTaskDetails(taskId)
        }

        binding.editDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.editTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.saveChangesBtn.setOnClickListener {
            updateTask()
        }
    }

    private fun loadTaskDetails(taskId: Long) {
        val task = TaskDatabase.getInstance(this).getTaskDao().getTaskById(taskId)
        binding.editTitle.setText(task.title)
        binding.editDescription.setText(task.description)

        // Format and set the date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        task.date?.let {
            binding.editDate.text = dateFormat.format(it)
            calendar.time = it // Set calendar time to existing task date
            isDateSelected = true // Date is selected if it exists
        }

        // Set the time
        binding.editTime.text = task.time ?: ""
        isTimeSelected = task.time != null // Time is selected if it exists
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(this)
        datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
            isDateSelected = true
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.editDate.text = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        }

        datePicker.show()
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerDialog(this,
            { _, hourOfDay, minute ->
                isTimeSelected = true
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val hour = hourOfDay % 12
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                binding.editTime.text =
                    String.format("%02d:%02d %s", if (hour == 0) 12 else hour, minute, amPm)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )

        timePicker.show()
    }

    private fun updateTask() {
        val updatedTitle = binding.editTitle.text.toString()
        val updatedDescription = binding.editDescription.text.toString()

        // Use the selected date and time, or retain existing values if not selected
        val updatedDate = if (isDateSelected) calendar.time else null
        val updatedTime = if (isTimeSelected) binding.editTime.text.toString() else null

        // Update the task in the database
        val task = TaskDatabase.getInstance(this).getTaskDao().getTaskById(taskId)
        task.title = updatedTitle
        task.description = updatedDescription
        task.date = updatedDate
        task.time = updatedTime

        TaskDatabase.getInstance(this).getTaskDao().updateTask(task)

        // Set result and finish the activity
        setResult(RESULT_OK)
        finish()
    }
}
