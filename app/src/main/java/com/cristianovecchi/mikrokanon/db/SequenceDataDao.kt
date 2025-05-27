package com.cristianovecchi.mikrokanon.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SequenceDataDao {

    @Query("SELECT * FROM sequence_data")
    fun getAll(): Flow<List<SequenceData>>

    @Insert
    fun insertAll(vararg sequence_data: SequenceData)

    @Delete
    fun delete(sequence_data: SequenceData)
}