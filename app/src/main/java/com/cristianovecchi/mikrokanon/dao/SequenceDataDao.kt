package com.cristianovecchi.mikrokanon.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SequenceDataDao {

    @Query("SELECT * FROM sequencedata")
    fun getAll(): Flow<List<SequenceData>>

    @Insert
    fun insertAll(vararg sequencedata: SequenceData)

    @Delete
    fun delete(sequencedata: SequenceData)
}