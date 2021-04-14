package com.cristianovecchi.mikrokanon

import android.app.Application
import com.cristianovecchi.mikrokanon.dao.SequenceDB
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository

class MikroKanonApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { SequenceDB.getDatabase(this) }
    val repository by lazy { SequenceDataRepository(database.sequenceDataDao()) }
}