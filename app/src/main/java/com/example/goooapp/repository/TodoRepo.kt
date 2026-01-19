package com.example.goooapp.repository



import com.example.goooapp.Data.TodoDao
import com.example.goooapp.model.TodoItem
import kotlinx.coroutines.flow.Flow

class TodoRepository(
    private val dao: TodoDao
) {

    val tasks: Flow<List<TodoItem>> = dao.getAllTasks()

    suspend fun insertTask(task: TodoItem) {
        dao.insertTask(task)
    }

    suspend fun deleteTask(task: TodoItem) {
        dao.deleteTask(task)
    }

    suspend fun clearCompletedTasks(){
        dao.deleteCompletedTasks()
    }

    suspend fun updateTask(task: TodoItem){
        dao.updateTask(task)
    }
}
