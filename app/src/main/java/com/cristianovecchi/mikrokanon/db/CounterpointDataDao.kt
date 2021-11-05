package com.cristianovecchi.mikrokanon.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterpointDataDao {
    @Query("SELECT * FROM counterpoints_data")
    fun getAllCounterpoints(): Flow<List<CounterpointData>>

    @Insert
    fun insertAllCounterpoints(vararg counterpointData: CounterpointData)

    @Delete
    fun deleteCounterpoint(counterpointData: CounterpointData)

    @Update
    fun updateCounterpoint(counterpointData: CounterpointData)

    @Query("DELETE FROM counterpoints_data")
    fun deleteAllCounterpoints()
}