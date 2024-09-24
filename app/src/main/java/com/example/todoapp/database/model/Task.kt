package com.example.todoapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity
data class Task(

    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var title: String? = null,

    var description: String? = null,

    var date: Date? = null,
    var time: String? = null,
    var isDone: Boolean? = null


)
