package com.cristianovecchi.mikrokanon.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SequenceData::class, UserOptionsData::class], version = 2, exportSchema = false)
@TypeConverters(ClipConverters::class)
public abstract class SequenceDB : RoomDatabase() {

    abstract fun sequenceDataDao(): SequenceDataDao
    abstract fun userOptionsDataDao(): UserOptionsDataDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SequenceDB? = null

        fun getDatabase(context: Context): SequenceDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SequenceDB::class.java,
                    "sequencedata_database"
                )
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

