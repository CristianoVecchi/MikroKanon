package com.cristianovecchi.mikrokanon.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class UserOptionsDataRepository(private val userOptionsDataDao: UserOptionsDataDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val userOptions: Flow<List<UserOptionsData>> = userOptionsDataDao.getAllUserOptions()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUserOptions(userOptionsData: UserOptionsData) {
        userOptionsDataDao.insertAllUserOptions(userOptionsData)
        println("UserOptions INSERTED: ${userOptionsData.toString()}")
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteUserOptions(userOptionsData: UserOptionsData) {
        userOptionsDataDao.deleteUserOptions(userOptionsData)
        println("userOptions DELETED: ${userOptionsData.toString()}")

    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllUserOptions() {
        userOptionsDataDao.deleteAllUserOptions()
        println("All userOptions DELETED")

    }
}