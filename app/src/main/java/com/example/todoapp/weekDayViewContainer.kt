package com.example.todoapp

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer

class weekDayViewContainer(private val itemWeekDayView: View) : ViewContainer(itemWeekDayView) {

    val weekDayTextView: TextView = itemWeekDayView.findViewById(R.id.week_day)
    val dayInMonthTextView: TextView = itemWeekDayView.findViewById(R.id.day_in_month)
}