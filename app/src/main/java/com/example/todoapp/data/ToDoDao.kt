package com.example.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.data.models.ToDoData

@Dao
interface ToDoDao {

    @Query("select * from todo_table order by id asc")
    fun getAllData(): LiveData<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(toDoData: ToDoData)

    @Update
    suspend fun updateData(toDoData: ToDoData)

    @Delete
    suspend fun deleteData(toDoData: ToDoData)

    @Query("delete from todo_table")
    suspend fun deleteAllData()

    @Query("select * from todo_table where title like :searhQuery ")
     fun searchDatabase(searhQuery: String):LiveData<List<ToDoData>>


     @Query("select * from todo_table order by case when priority like 'H%' THEN 1  when priority like 'M%' THEN 2  when priority like 'L%' THEN 3 END")
     fun sortByHighPriority():LiveData<List<ToDoData>>

    @Query("select * from todo_table order by case when priority like 'L%' THEN 1  when priority like 'M%' THEN 2  when priority like 'H%' THEN 3 END")
    fun sortByLowPriority():LiveData<List<ToDoData>>

}