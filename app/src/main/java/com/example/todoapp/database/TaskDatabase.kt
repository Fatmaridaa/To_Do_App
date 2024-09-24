package com.example.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.todoapp.database.TypeConverter.DateConverter
import com.example.todoapp.database.dao.TaskDao
import com.example.todoapp.database.model.Task


@Database([Task::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao


    companion object {

        //singleton design pattern
        private var INSTANCE: TaskDatabase? = null
        private var DATABASE_NAME = "Tasks Database"
        fun getInstance(context: Context): TaskDatabase {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, TaskDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE!!

        }
    }
}