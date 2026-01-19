package com.example.goooapp.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goooapp.model.TodoItem
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoItem)

    @Delete
    suspend fun deleteTask(task: TodoItem)

    @Query("SELECT* FROM  todo_table ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TodoItem>>

    @Query("DELETE FROM todo_table WHERE isDone = 1")
    suspend fun deleteCompletedTasks()

    @Update
    suspend fun updateTask(task: TodoItem)

}