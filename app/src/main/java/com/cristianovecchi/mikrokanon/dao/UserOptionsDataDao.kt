package com.cristianovecchi.mikrokanon.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserOptionsDataDao {
    @Query("SELECT * FROM useroptionsdata")
    fun getAllUserOptions(): Flow<List<UserOptionsData>>

    @Insert
    fun insertAllUserOptions(vararg userOptionsData: UserOptionsData)

    @Delete
    fun deleteUserOptions(userOptionsData: UserOptionsData)

    @Query("DELETE FROM useroptionsdata")
    fun deleteAllUserOptions()
}