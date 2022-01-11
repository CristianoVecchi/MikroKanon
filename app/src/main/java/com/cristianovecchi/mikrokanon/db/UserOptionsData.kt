package com.cristianovecchi.mikrokanon.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



    @Entity(tableName = "useroptions_data")
    data class UserOptionsData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long,

        @ColumnInfo(name = "ensemble_types")
        val ensembleTypes: String,

        @ColumnInfo(name = "range_types")
        val rangeTypes: String,

        @ColumnInfo(name = "legato_types")
        val legatoTypes: String,

        @ColumnInfo(name = "melody_types")
        val melodyTypes: String,

        @ColumnInfo(name = "glissando_flags")
        val glissandoFlags: Int,

        @ColumnInfo(name = "vibrato")
        val vibrato: Int,

        @ColumnInfo(name = "dynamics")
        val dynamics: String,

        @ColumnInfo(name = "bpms")
        val bpms: String,

        @ColumnInfo(name = "rhythm")
        val rhythm: String,

        @ColumnInfo(name = "rhythm_shuffle")
        val rhythmShuffle: Int,

        @ColumnInfo(name = "parts_shuffle")
        val partsShuffle: Int,

        @ColumnInfo(name = "rowForms")
        val rowForms: String,
//        @ColumnInfo(name = "rowforms_flags")// row forms + separator
//        val rowFormsFlags: Int,

        @ColumnInfo(name = "ritornello")
        val ritornello: Int,

        @ColumnInfo(name = "transpose")
        val transpose: String,

        @ColumnInfo(name = "doubling_flags")
        val doublingFlags: Int,

        @ColumnInfo(name = "(audio8D_flags")
        val audio8DFlags: Int,

        @ColumnInfo(name = "intSetVert_flags")
        val intSetVertFlags: Int,

        @ColumnInfo(name = "intSetHor_flags")
        val intSetHorFlags: Int,

        @ColumnInfo(name = "spread")
        val spread: Int,

        @ColumnInfo(name = "deep_search")
        val deepSearch: Int,

        @ColumnInfo(name = "detector_flags")
        val detectorFlags: Int,

        @ColumnInfo(name = "detector_extension")
        val detectorExtension: Int,

        @ColumnInfo(name = "colors")
        val colors: String,

        @ColumnInfo(name = "language")
        val language: String,

        @ColumnInfo(name = "zodiac_flags")
        val zodiacFlags: Int,

        @ColumnInfo(name = "nuances")
        val nuances: Int,
    ){
        companion object{
            fun getDefaultUserOptionsData(): UserOptionsData{
                return UserOptionsData(0,"2", "2|0","4|0","0",
                    0,0,
                    "0.828", "90", "4|1",
                    0,0, "1|1",0, "0",
                    0,0,0b0011110, 0b1111111,
                    0,0,0,1,
                     //"System|0" ,
                     "579|0" , // priority on custom color 579
                    "System", 0, 1 )
            }
        }
    }

