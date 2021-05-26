package com.cristianovecchi.mikrokanon.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



    @Entity(tableName = "useroptions_data")
    data class UserOptionsData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long,

        @ColumnInfo(name = "ensemble_type")
        val ensembleType: String,

        @ColumnInfo(name = "bpm")
        val bpm: String,

        @ColumnInfo(name = "rhythm")
        val rhythm: String,

        @ColumnInfo(name = "rhythm_shuffle")
        val rhythmShuffle: String,

        @ColumnInfo(name = "parts_shuffle")
        val partsShuffle: String,

        @ColumnInfo(name = "rowforms_flags")
        val rowFormsFlags: String
    ){
        companion object{
            fun getDefaultUserOptionData(): UserOptionsData{
                return UserOptionsData(0,"0","90","1","0","0", "1")
            }
        }
    }
