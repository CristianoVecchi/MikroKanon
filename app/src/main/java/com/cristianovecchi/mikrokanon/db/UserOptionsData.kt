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
        val ensembleType: Int,

        @ColumnInfo(name = "bpm")
        val bpm: Int,

        @ColumnInfo(name = "rhythm")
        val rhythm: Int,

        @ColumnInfo(name = "rhythm_shuffle")
        val rhythmShuffle: Int,

        @ColumnInfo(name = "parts_shuffle")
        val partsShuffle: Int,

        @ColumnInfo(name = "rowforms_flags")
        val rowFormsFlags: Int,

        @ColumnInfo(name = "doubling_flags")
        val doublingFlags: Int,

        @ColumnInfo(name = "language")
        val language: String
    ){
        companion object{
            fun getDefaultUserOptionData(): UserOptionsData{
                return UserOptionsData(0,0,90,1,
                    1,1, 1, 0,"System")
            }
        }
    }
