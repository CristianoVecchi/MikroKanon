package com.cristianovecchi.mikrokanon.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(entities = [SequenceData::class, UserOptionsData::class, CounterpointData::class], version = 3, exportSchema = false)
@TypeConverters(ClipConverters::class)
public abstract class MikroKanonDB : RoomDatabase() {

    abstract fun sequenceDataDao(): SequenceDataDao
    abstract fun userOptionsDataDao(): UserOptionsDataDao
    abstract fun counterpointDataDao(): CounterpointDataDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: MikroKanonDB? = null

        fun getDatabase(context: Context): MikroKanonDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context): MikroKanonDB {
            return Room.databaseBuilder(context, MikroKanonDB::class.java, "mikrokanon_database")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        //pre-populate data
                        Executors.newSingleThreadExecutor().execute {
                            instance?.sequenceDataDao()?.insertAll(*DataGenerator.getSequencesData().toTypedArray())
                        }
                        Executors.newSingleThreadExecutor().execute {
                            instance?.counterpointDataDao()?.insertAllCounterpoints(
                                *DataGenerator.getCounterpointsData().toTypedArray()
                            )
                        }
                        Executors.newSingleThreadExecutor().execute {
                            instance?.userOptionsDataDao()?.insertAllUserOptions(
                                *DataGenerator.getUserOptionsData().toTypedArray()
                            )
                        }
                    }
                })
                .build()
        }
    }
}

class DataGenerator {

    companion object {
        fun getSequencesData(): List<SequenceData>{
            return listOf(
                SequenceData(0, listOf(
                    ClipData(0,2,2,0),
                    ClipData(1,9,9,0),
                    ClipData(2,5,5,0),
                    ClipData(3,2,2,0),
                    ClipData(4,1,0,1),
                    ClipData(5,2,2,0),
                    ClipData(6,4,4,0),
                    ClipData(7,5,5,0),
                    ClipData(8,7,7,0),
                    ClipData(9,5,5,0),
                    ClipData(10,4,4,0),
                    ClipData(11,2,2,0),
                )),
                SequenceData(0, listOf(
                    ClipData(0,1,0,1),
                    ClipData(1,11,11,0),
                    ClipData(2,10,9,1),
                    ClipData(3,9,9,0),
                    ClipData(4,8,7,1),
                    ClipData(5,7,7,0),
                    ClipData(6,9,9,0),
                    ClipData(7,11,11,0),
                    ClipData(8,0,0,0),
                    ClipData(9,1,0,1),
                    ClipData(10,3,2,1), // Re#
                    ClipData(11,8,7,1),
                    ClipData(12,4,4,0),
                    ClipData(13,8,7,1),
                    ClipData(14,11,11,0),
                    ClipData(15,11,11,0),
                    ClipData(16,1,0,1),
                    ClipData(17,10,9,1),
                )),
                SequenceData(0, listOf(
                    ClipData(0,0,0,0),
                    ClipData(1,11,11,0),
                    ClipData(2,0,0,0),
                    ClipData(3,11,11,0),
                    ClipData(4,7,7,0),
                    ClipData(5,4,4,0),
                    ClipData(6,11,11,0),
                    ClipData(7,9,9,0),

                    ClipData(8,0,0,0),
                    ClipData(9,11,11,0),
                    ClipData(10,0,0,0),
                    ClipData(11,11,11,0),
                    ClipData(12,9,9,0),
                    ClipData(13,2,2,0),
                    ClipData(14,7,7,0),
                    ClipData(15,9,9,0),

                    ClipData(16,0,0,0),
                    ClipData(17,11,11,0),
                    ClipData(18,0,0,0),
                    ClipData(19,11,11,0),
                    ClipData(20,7,7,0),
                    ClipData(21,4,4,0),
                    ClipData(22,11,11,0),
                    ClipData(23,9,9,0),
                )),
                SequenceData(0, listOf(
                    ClipData(0,3,4,-1),
                    ClipData(1,10,11,-1),
                    ClipData(2,2,2,0),
                    ClipData(3,5,5,0),
                    ClipData(4,4,4,0),
                    ClipData(5,0,0,0),
                    ClipData(6,6,5,1),
                    ClipData(7,8,9,-1),
                    ClipData(8,1,2,-1),
                    ClipData(9,9,9,0),
                    ClipData(10,11,11,0),
                    ClipData(11,7,7,0),
                )),
            )
        }
        fun getUserOptionsData(): List<UserOptionsData>{
            return listOf(
                UserOptionsData.getDefaultUserOptionsData()
            )
        }
        fun getCounterpointsData(): List<CounterpointData>{
            return List(16){ CounterpointData.getDefaultCounterpointData()}
        }
    }

}

