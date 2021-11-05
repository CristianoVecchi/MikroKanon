package com.cristianovecchi.mikrokanon.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "counterpoints_data")
data class CounterpointData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "parts")
    val parts: String,

){
    companion object{
        fun getDefaultCounterpointData(): CounterpointData{
            return CounterpointData(0, "")
        }
    }
}
