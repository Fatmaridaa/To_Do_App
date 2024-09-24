package com.example.todoapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.database.model.Task
import java.util.Date

@Dao
interface TaskDao {

    @Insert
    fun insertTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("select * from task")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM Task WHERE date = :date")
    fun getTasksByDate(date: Date): List<Task>


    @Query("SELECT * FROM Task WHERE id = :taskId")
    fun getTaskById(taskId: Long): Task
}