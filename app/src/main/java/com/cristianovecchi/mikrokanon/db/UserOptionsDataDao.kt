package com.cristianovecchi.mikrokanon.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserOptionsDataDao {
    @Query("SELECT * FROM useroptions_data")
    fun getAllUserOptions(): Flow<List<UserOptionsData>>

    @Insert
    fun insertAllUserOptions(vararg userOptionsData: UserOptionsData)

    @Delete
    fun deleteUserOptions(userOptionsData: UserOptionsData)

    @Query("DELETE FROM useroptions_data")
    fun deleteAllUserOptions()
}