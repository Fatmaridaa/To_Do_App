package com.route.todoappc40gsunwed.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.model.Task
import com.example.todoapp.databinding.ItemTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskAdapter(
    var tasksList: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit,


    ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskTime.text = task.time
            binding.taskTitle.text = task.title


            if (task.isDone == true) {
                binding.taskDone.visibility = View.GONE
                binding.itemTxt.visibility = View.VISIBLE
                updateUIAsDone()
            }
        }

        fun updateUIAsDone() {
            binding.taskStatusDivider.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, R.color.green)
            )
            binding.taskTitle.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.green)
            )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val item = tasksList[position]
        holder.bind(item)

        holder.binding.taskDone.setOnClickListener {
            item.isDone = true

            // Update in the database
            CoroutineScope(Dispatchers.IO).launch {
                TaskDatabase.getInstance(holder.binding.root.context)
                    .getTaskDao().updateTask(item)
            }

            holder.binding.taskDone.visibility = View.GONE
            holder.binding.itemTxt.visibility = View.VISIBLE
            holder.updateUIAsDone()
        }



        holder.binding.dragItem.setOnClickListener {

            onTaskClick(item)

        }

        holder.binding.leftView.setOnClickListener {
            onTaskDelete(item)
        }
    }

    fun updateList(tasks: List<Task>) {
        tasksList = tasks
        notifyDataSetChanged()
    }
}

