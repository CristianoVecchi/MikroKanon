package com.cristianovecchi.mikrokanon.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class CounterpointDataRepository(private val counterpointDataDao: CounterpointDataDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val counterpoints: Flow<List<CounterpointData>> = counterpointDataDao.getAllCounterpoints()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCounterpoint(counterpointData: CounterpointData) {
        counterpointDataDao.insertAllCounterpoints(counterpointData)
        //println("UserOptions INSERTED: ${userOptionsData.toString()}")
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateCounterpoint(counterpointData: CounterpointData) {
        counterpointDataDao.updateCounterpoint(counterpointData)
        //println("UserOptions INSERTED: ${userOptionsData.toString()}")
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteCounterpoint(counterpointData: CounterpointData) {
        counterpointDataDao.deleteCounterpoint(counterpointData)
        //println("userOptions DELETED: ${userOptionsData.toString()}")

    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllCounterpoints() {
        counterpointDataDao.deleteAllCounterpoints()
        //println("All userOptions DELETED")

    }
}