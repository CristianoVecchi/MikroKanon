package com.cristianovecchi.mikrokanon.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "useroptions_data")
    data class UserOptionsData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long,

        @ColumnInfo(name = "ensembles_list")
        val ensemblesList: String,

        @ColumnInfo(name = "range_types")
        val rangeTypes: String,

        @ColumnInfo(name = "legato_types")
        val legatoTypes: String,

        @ColumnInfo(name = "melody_types")
        val melodyTypes: String,

        @ColumnInfo(name = "glissando_flags")
        val glissandoFlags: String,

        @ColumnInfo(name = "vibrato")
        val vibrato: String,

        @ColumnInfo(name = "dynamics")
        val dynamics: String,

        @ColumnInfo(name = "bpms")
        val bpms: String,

        @ColumnInfo(name = "rhythm")
        val rhythm: String,

        @ColumnInfo(name = "swing_shuffle")
        val swingShuffle: String,

        @ColumnInfo(name = "rhythm_shuffle")
        val rhythmShuffle: String,

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
        val doublingFlags: String,

        @ColumnInfo(name = "(audio8D_flags")
        val audio8DFlags: String,

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

        @ColumnInfo(name = "counterpoint_view")
        val counterpointView: Int,

        @ColumnInfo(name = "language")
        val language: String,

        @ColumnInfo(name = "zodiac_flags")
        val zodiacFlags: Int,

        @ColumnInfo(name = "nuances")
        val nuances: String,

        @ColumnInfo(name = "check_and_replace")
        val checkAndReplace: String,

        @ColumnInfo(name = "harmonizations")
        val harmonizations: String,

        @ColumnInfo(name = "chords_to_enhance")
        val chordsToEnhance: String,

        @ColumnInfo(name = "enhance_chords_in_transpositions")
        val enhanceChordsInTranspositions: Int,

        @ColumnInfo(name = "drums")
        val drums: String,

        @ColumnInfo(name = "lastplay_data")
        val lastPlayData: String,

        @ColumnInfo(name = "privacy")
        val privacy: Int,
    ){
        companion object{
            fun getDefaultUserOptionsData(): UserOptionsData {
                return UserOptionsData(0,"2", "2|0","4|1","0",
                    "0","0",
                    "0.828", "90", "0|1", "0.5", "0",0, "1|1",0, "0|1",
                    "0","0",0b0011110, 0b1111111,
                    0,0,0,1,
                     //"System|0" ,
                     "515|0" , // priority on custom color 515
                    0,"System", 0, "1" ,
                    ";;;;;;","","0|1",0,
                    "0","0|-1", 0)
            }
            fun updateUserOptionsData(
                optionsDataClone: UserOptionsData,
                key: String,
                value: Any,
                id: Long = optionsDataClone.id
            ): UserOptionsData {
                return when (key) {
                    "ensemblesList" -> {
                        optionsDataClone.copy(id = id, ensemblesList = value as String)
                    }
                    "rangeTypes" -> {
                        optionsDataClone.copy(id = id, rangeTypes = value as String)
                    }
                    "legatoTypes" -> {
                        optionsDataClone.copy(id = id, legatoTypes = value as String)
                    }
                    "melodyTypes" -> {
                        optionsDataClone.copy(id = id, melodyTypes = value as String)
                    }
                    "glissandoFlags" -> {
                        optionsDataClone.copy(id = id, glissandoFlags = value as String)
                    }
                    "vibrato" -> {
                        optionsDataClone.copy(id = id, vibrato = value as String)
                    }
                    "dynamics" -> {
                        optionsDataClone.copy(id = id, dynamics = value as String)
                    }
                    "bpms" -> {
                        optionsDataClone.copy(id = id, bpms = value as String)
                    }
                    "rhythm" -> {
                        optionsDataClone.copy(id = id, rhythm = value as String)
                    }
                    "swingShuffle" -> {
                        optionsDataClone.copy(id = id, swingShuffle = value as String)
                    }
                    "rhythmShuffle" -> {
                        optionsDataClone.copy(id = id, rhythmShuffle = value as String)
                    }
                    "partsShuffle" -> {
                        optionsDataClone.copy(id = id, partsShuffle = value as Int)
                    }
                    "rowForms" -> {
                        optionsDataClone.copy(id = id, rowForms = value as String)
                    }
//            "rowFormsFlags" -> {
//                var flags = value as Int
//                flags = if(flags and 0b10000 > 0 && flags and 0b1110 == 0) 1 else flags // deactivate separator if row forms are unactive
//                newUserOptionsData  = optionsDataClone.copy(rowFormsFlags = flags)
//            }
                    "ritornello" -> {
                        optionsDataClone.copy(id = id, ritornello = value as Int)
                    }
                    "transpose" -> {
                        optionsDataClone.copy(id = id, transpose = value as String)
                    }
                    "doublingFlags" -> {
                        optionsDataClone.copy(id = id, doublingFlags = value as String)
                    }
                    "audio8DFlags" -> {
                        optionsDataClone.copy(id = id, audio8DFlags = value as String)
                    }
                    "intSetVertFlags" -> {
                        optionsDataClone.copy(id = id, intSetVertFlags = value as Int)
                    }
                    "intSetHorFlags" -> {
                        optionsDataClone.copy(id = id, intSetHorFlags = value as Int)
                    }
                    "spread" -> {
                        optionsDataClone.copy(id = id, spread = value as Int)
                    }
                    "deepSearch" -> {
                        optionsDataClone.copy(id = id, deepSearch = value as Int)
                    }
                    "detectorFlags" -> {
                        optionsDataClone.copy(id = id, detectorFlags = value as Int)
                    }
                    "detectorExtension" -> {
                        optionsDataClone.copy(id = id, detectorExtension = value as Int)
                    }
                    "colors" -> {
                        optionsDataClone.copy(id = id, colors = value as String)
                    }
                    "counterpointView" -> {
                        optionsDataClone.copy(id = id, counterpointView = value as Int)
                    }
                    "language" -> {
                        optionsDataClone.copy(id = id, language = value as String)
                    }
                    "zodiacFlags" -> {
                        optionsDataClone.copy(id = id, zodiacFlags = value as Int)
                    }
                    "nuances" -> {
                        optionsDataClone.copy(id = id, nuances = value as String)
                    }
                    "checkAndReplace" -> {
                        optionsDataClone.copy(id = id, checkAndReplace = value as String)
                    }
                    "harmonizations" -> {
                        optionsDataClone.copy(id = id, harmonizations = value as String)
                    }
                    "chordsToEnhance" -> {
                        optionsDataClone.copy(id = id, chordsToEnhance = value as String)
                    }
                    "enhanceChordsInTranspositions" -> {
                        optionsDataClone.copy(id = id, enhanceChordsInTranspositions = value as Int)
                    }
                    "drums" -> {
                        optionsDataClone.copy(id = id, drums = value as String)
                    }
                    "lastPlayData" -> {
                        optionsDataClone.copy(id = id, lastPlayData = value as String)
                    }
                    "privacy" -> {
                        optionsDataClone.copy(id = id, privacy = value as Int)
                    }
                    else -> optionsDataClone.copy()
                }
            }
        }
    }

