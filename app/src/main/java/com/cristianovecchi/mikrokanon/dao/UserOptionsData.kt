package com.cristianovecchi.mikrokanon.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



    @Entity(tableName = "useroptionsdata")
    data class UserOptionsData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long,

        @ColumnInfo(name = "ensembletype")
        val ensembleType: String,

        @ColumnInfo(name = "bpm")
        val bpm: String,

        @ColumnInfo(name = "rhythm")
        val rhythm: String
    )
