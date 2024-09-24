package com.example.todoapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.todoapp.callbacks.OnTaskAddedListener
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.fragments.AddTaskBottomFragment
import com.example.todoapp.fragments.ListFragment
import com.example.todoapp.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var todoListFragment: ListFragment
    lateinit var settingsFragment: SettingsFragment
    fun pushFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.todoFragmentContainer.id, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        todoListFragment = ListFragment()
        settingsFragment = SettingsFragment()
        binding.fabAdd.setOnClickListener {
            val bottomSheetFragment = AddTaskBottomFragment()
            bottomSheetFragment.onTaskAddedListener = object : OnTaskAddedListener {
                override fun onTaskAdded() {
                    if (todoListFragment.isVisible)
                        if (todoListFragment.selectedDate == null)
                            todoListFragment.getTasksFromDatabase()
                        else
                            todoListFragment.getTasksByDate(todoListFragment.calendar.time)
                }
            }
            bottomSheetFragment.show(supportFragmentManager, "add_todo")

        }
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_list -> pushFragment(todoListFragment)
                R.id.navigation_settings -> pushFragment(settingsFragment)
            }
            return@setOnItemSelectedListener true
        }
        binding.bottomNavbar.selectedItemId = R.id.navigation_list

    }

}