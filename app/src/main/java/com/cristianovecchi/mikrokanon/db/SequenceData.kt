package com.cristianovecchi.mikrokanon.db

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "sequence_data")
data class SequenceData(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @TypeConverters(ClipConverters::class)
    @ColumnInfo(name = "clips")
    val clips: List<ClipData>
)

data class ClipData(
    val text: String,
    val clipId: Int,
    var abstractNote: Int,
    val name: Int,
    val ax: Int
)

class ClipConverters {

    companion object {
        @JvmStatic
        @TypeConverter
        fun listToString(list: List<ClipData?>?): String {
            val gson = Gson()
            return gson.toJson(list)
        }

        @JvmStatic
        @TypeConverter
        fun fromString(value: String?): List<ClipData> {
            val listType = object :
                TypeToken<List<ClipData?>?>() {}.type
            return Gson()
                .fromJson<List<ClipData>>(value, listType)
        }


    }
}




