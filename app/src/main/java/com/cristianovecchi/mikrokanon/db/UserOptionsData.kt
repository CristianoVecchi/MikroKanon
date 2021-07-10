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

        @ColumnInfo(name = "intSetVert_flags")
        val intSetVertFlags: Int,

        @ColumnInfo(name = "intSetHor_flags")
        val intSetHorFlags: Int,

        @ColumnInfo(name = "spread")
        val spread: Int,

        @ColumnInfo(name = "deep_search")
        val deepSearch: Int,

        @ColumnInfo(name = "language")
        val language: String,
    ){
        companion object{
            fun getDefaultUserOptionsData(): UserOptionsData{
                return UserOptionsData(0,2,90,3,
                    0,0, 1,
                    0,0b0011110, 0b1111111,
                    0,0,"System" )
            }
        }
    }
