package com.example.todoapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todoapp.callbacks.OnTaskAddedListener
import com.example.todoapp.clearTime
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.model.Task
import com.example.todoapp.databinding.FragmentAddTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar


class AddTaskBottomFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentAddTodoBinding
    lateinit var calendar: Calendar
    var isDateSelected = false
    var isTimeSelected = false
    var onTaskAddedListener: OnTaskAddedListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        initViews()
    }

    fun initViews() {
        binding.addTaskBtn.setOnClickListener {

            if (validateFields()) {
                calendar.clearTime()

                val task = Task(
                    null,
                    binding.titleEditText.text.toString(),
                    binding.descriptionEditText.text.toString(),
                    calendar.time,
                    binding.selectTime.text.toString(),
                    false

                )


                TaskDatabase
                    .getInstance(requireContext())
                    .getTaskDao()
                    .insertTask(task)
                onTaskAddedListener?.onTaskAdded()
                dismiss()

            }

        }

        binding.selectDate.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext())
            datePicker.setOnDateSetListener { view, year, month, dayOfMonth ->
                isDateSelected = true
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.selectDate.text = "$dayOfMonth/${month + 1}/$year"
            }

            datePicker.show()
        }

        binding.selectTime.setOnClickListener {

            val timePicker = TimePickerDialog(requireContext(),
                { view, hourOfDay, minute ->

                    isTimeSelected = true

                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    var hour = hourOfDay % 12
                    if (hour == 0) hour = 12
                    val _am_pm = if ((hourOfDay > 12)) "PM" else "AM"
                    binding.selectTime.text = "${hour}:${minute} $_am_pm"

                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )

            timePicker.show()

        }
    }

    fun validateFields(): Boolean {
        if (binding.titleEditText.text.isEmpty() || binding.titleEditText.text.isBlank()) {
            binding.titleEditText.error = "Required"
            return false
        } else
            binding.titleEditText.error = null

        if (binding.descriptionEditText.text.isEmpty() || binding.descriptionEditText.text.isBlank()) {
            binding.descriptionEditText.error = "Required"
            return false
        } else
            binding.descriptionEditText.error = null


        if (!isDateSelected) {
            Toast.makeText(requireContext(), "Date is Required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isTimeSelected) {
            Toast.makeText(requireContext(), "Time is Required", Toast.LENGTH_SHORT).show()
            return false
        }






        return true
    }

}