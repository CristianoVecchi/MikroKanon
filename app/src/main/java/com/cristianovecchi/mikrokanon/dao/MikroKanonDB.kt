package com.cristianovecchi.mikrokanon.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(entities = [SequenceData::class, UserOptionsData::class], version = 2, exportSchema = false)
@TypeConverters(ClipConverters::class)
public abstract class MikroKanonDB : RoomDatabase() {

    abstract fun sequenceDataDao(): SequenceDataDao
    abstract fun userOptionsDataDao(): UserOptionsDataDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: MikroKanonDB? = null

        fun getDatabase(context: Context): MikroKanonDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context): MikroKanonDB {
            return Room.databaseBuilder(context, MikroKanonDB::class.java, "kikrokanon_database")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        //pre-populate data
                        Executors.newSingleThreadExecutor().execute {
                            instance?.sequenceDataDao()?.insertAll(*DataGenerator.getSequencesData().toTypedArray())
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
                SequenceData(0, listOf<ClipData>(
                    ClipData("Re",0,2,2,0),
                    ClipData("La",1,9,9,0),
                    ClipData("Fa",2,5,5,0),
                    ClipData("Re",3,2,2,0),
                    ClipData("Do#",4,1,0,1),
                    ClipData("Re",5,2,2,0),
                    ClipData("Mi",6,4,4,0),
                    ClipData("Fa",7,5,5,0),
                    ClipData("Sol",8,7,7,0),
                    ClipData("Fa",9,5,5,0),
                    ClipData("Mi",10,4,4,0),
                    ClipData("Re",11,2,2,0),
                ))
            )
        }
        fun getUserOptionsData(): List<UserOptionsData>{
            return listOf(
                UserOptionsData(0,"0","90","0","0","0" )
            )
        }
    }

}

