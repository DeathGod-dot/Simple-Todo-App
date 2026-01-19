package com.example.goooapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goooapp.DBase.TodoDatabase
import com.example.goooapp.model.TaskFilter
import com.example.goooapp.model.TodoItem
import com.example.goooapp.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: TodoRepository

    private var recentlyDeletedTask: TodoItem? = null

    val tasks: Flow<List<TodoItem>> by lazy {
        repository.tasks
    }

    var currentFilter by mutableStateOf(TaskFilter.ALL)
        private set

    fun setFilter(filter: TaskFilter){
        currentFilter = filter
    }

    val filteredTasks: Flow<List<TodoItem>> by lazy {
        combine(tasks, snapshotFlow { currentFilter }) { taskList, filter ->
            when (filter) {
                TaskFilter.ALL -> taskList
                TaskFilter.PENDING -> taskList.filter { !it.isDone }
                TaskFilter.COMPLETED -> taskList.filter { it.isDone }
            }
        }
    }

    init {
        val dao = TodoDatabase
            .getDatabase(application)
            .todoDao()

        repository = TodoRepository(dao)
    }

    fun addTask(title: String) {
        if (title.isBlank()) return

        viewModelScope.launch {
            repository.insertTask(
                TodoItem(title = title)
            )
        }
    }

    fun deleteTask(task: TodoItem, tasks1: List<TodoItem>) {
        recentlyDeletedTask = task
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun undoDelete() {
        recentlyDeletedTask?.let {
            viewModelScope.launch {
                repository.insertTask(it)
            }
        }
        recentlyDeletedTask = null
    }

    fun clearCompletedTasks(){
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    fun toggleTask(task: TodoItem, checked: Boolean) {
        viewModelScope.launch {
            repository.insertTask(
                task.copy(isDone = checked)
            )
        }
    }

    fun updateTask(task: TodoItem,newTitle: String){
        if (newTitle.isBlank())return


        viewModelScope.launch {
            repository.updateTask(
                task.copy(title = newTitle)
            )
        }
    }
}
