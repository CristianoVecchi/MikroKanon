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
        val rhythm: String,

        @ColumnInfo(name = "rhythmshuffle")
        val rhythmShuffle: String,

        @ColumnInfo(name = "partsshuffle")
        val partsShuffle: String
    ){
        companion object{
            fun getDefaultUserOptionData(): UserOptionsData{
                return UserOptionsData(0,"0","90","0","0","0")
            }
        }
    }
