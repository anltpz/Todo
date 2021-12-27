package com.example.todoapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.data.models.Priority
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "todo_table")
@Parcelize
data class ToDoData (

    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val title:String,
    val priority: Priority,
    val description: String) :Parcelable
