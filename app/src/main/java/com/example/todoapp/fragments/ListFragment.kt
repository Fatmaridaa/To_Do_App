package com.example.todoapp.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.todoapp.EditTaskActivity
import com.example.todoapp.R
import com.example.todoapp.clearTime
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.model.Task
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.weekDayViewContainer
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.WeekDayBinder
import com.route.todoappc40gsunwed.adapter.TaskAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ListFragment : Fragment() {

    lateinit var binding: FragmentListBinding
    lateinit var adapter: TaskAdapter
    var selectedDate: LocalDate? = null
    lateinit var calendar: Calendar

    private val EDIT_TASK_REQUEST_CODE = 1 // Define a request code for editing tasks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        initWeekCalendarView()
        getTasksFromDatabase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Refresh the task list if returning from EditTaskActivity
        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            getTasksFromDatabase()
        }
    }

    // Update to include task deletion logic
    fun getTasksFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = TaskDatabase.getInstance(requireContext()).getTaskDao().getAllTasks()
            withContext(Dispatchers.Main) {
                adapter = TaskAdapter(tasks, { task ->
                    // Task click logic for editing
                    val intent = Intent(requireContext(), EditTaskActivity::class.java)
                    intent.putExtra("TASK_ID", task.id)
                    startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
                }, { task ->
                    // Task delete logic
                    deleteTask(task)
                })
                binding.tasksRecyclerView.adapter = adapter
            }
        }
    }

    // Function to delete task from the database and update UI
    private fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            TaskDatabase.getInstance(requireContext()).getTaskDao().deleteTask(task)

            withContext(Dispatchers.Main) {
                getTasksFromDatabase() // Refresh the task list after deletion
            }
        }
    }

    fun initWeekCalendarView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bindWeekCalendarView()
            val currentDate = LocalDate.now()
            val currentMonth = YearMonth.now()
            val startDate = currentMonth.minusMonths(100).atStartOfMonth() // Adjust as needed
            val endDate = currentMonth.plusMonths(100).atEndOfMonth() // Adjust as needed
            val firstDayOfWeek =
                firstDayOfWeekFromLocale(Locale.forLanguageTag("ar")) // Available from the library
            binding.weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
            binding.weekCalendarView.scrollToWeek(currentDate)
        }
    }

    fun bindWeekCalendarView() {
        binding.weekCalendarView.dayBinder = object : WeekDayBinder<weekDayViewContainer> {
            override fun bind(container: weekDayViewContainer, data: WeekDay) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val blue = ResourcesCompat.getColor(resources, R.color.blue, null)
                    val textColor =
                        ResourcesCompat.getColor(resources, R.color.calendarTextColor, null)

                    container.weekDayTextView.text = data.date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                    container.dayInMonthTextView.text = "${data.date.dayOfMonth}"
                    if (data.date == selectedDate) {
                        container.dayInMonthTextView.setTextColor(blue)
                        container.weekDayTextView.setTextColor(blue)
                    } else {
                        container.dayInMonthTextView.setTextColor(textColor)
                        container.weekDayTextView.setTextColor(textColor)
                    }
                    container.view.setOnClickListener {
                        Log.e("TAG", "bind:calendar Month ${calendar.get(Calendar.MONTH)}")
                        Log.e("TAG", "bind: Local Date Month ${data.date.month.value}")
                        selectedDate = data.date
                        binding.weekCalendarView.notifyWeekChanged(data)
                        calendar.set(Calendar.YEAR, data.date.year)
                        calendar.set(Calendar.MONTH, data.date.month.value - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, data.date.dayOfMonth)
                        calendar.clearTime()
                        getTasksByDate(calendar.time)
                    }
                }
            }

            override fun create(view: View): weekDayViewContainer {
                return weekDayViewContainer(view)
            }
        }

        binding.weekCalendarView.weekScrollListener = { week: Week ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val month =
                    week.days[0].date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                binding.monthNameText.text = month
            }
        }
    }

    fun getTasksByDate(date: Date) {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = TaskDatabase.getInstance(requireContext()).getTaskDao().getTasksByDate(date)
            withContext(Dispatchers.Main) {
                adapter.updateList(tasks)
            }
        }
    }
}
