package com.cristianovecchi.mikrokanon

import android.app.Application
import com.cristianovecchi.mikrokanon.dao.MikroKanonDB
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository
import com.cristianovecchi.mikrokanon.dao.UserOptionsDataRepository

class MikroKanonApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { MikroKanonDB.getDatabase(this) }
    val repository by lazy { SequenceDataRepository(database.sequenceDataDao()) }
    val userRepository by lazy { UserOptionsDataRepository(database.userOptionsDataDao()) }
}