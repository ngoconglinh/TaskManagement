package com.example.managementuikit.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task(@ColumnInfo(name = "Title_col") var title:String = "",
           @ColumnInfo(name = "Status_col") var status:String = "",
           @ColumnInfo(name = "Starts_Time_col") var starts_time:Long = 0,
           @ColumnInfo(name = "End_Time_col") var end_time:Long = 0,
           @ColumnInfo(name = "Participants_col") var participants: String = ""
):java.io.Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Task_ID_col")
    var id:Int = 0
}