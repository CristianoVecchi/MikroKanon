package com.cristianovecchi.mikrokanon.dao

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class SequenceDataRepository(private val sequenceDataDao: SequenceDataDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSequences: Flow<List<SequenceData>> = sequenceDataDao.getAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(sequenceData: SequenceData) {
        sequenceDataDao.insertAll(sequenceData)
        println("SequenceData INSERTED: ${sequenceData.clips.toString()}")
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(sequenceData: SequenceData) {
        val deleted = sequenceDataDao.delete(sequenceData)
        println("SequenceData DELETED: ${sequenceData.clips.toString()}")

    }
}