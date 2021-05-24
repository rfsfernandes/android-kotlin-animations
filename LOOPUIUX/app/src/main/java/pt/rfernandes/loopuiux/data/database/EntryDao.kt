package pt.rfernandes.loopuiux.data.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow
import pt.rfernandes.loopuiux.model.TravelEntry

/**
 * Interface EntryDao created at 5/23/21 16:01 for the project LOOP UI&UX
 * By: rodrigofernandes
 */
@Dao
interface EntryDao {
    @Query("SELECT * FROM entry_table ORDER BY id DESC")
    fun getEntries(): Flow<List<TravelEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(travelEntry: TravelEntry)

    @Query("DELETE FROM entry_table WHERE id = :id")
    suspend fun deleteEntry(id: Int)

    @Query("DELETE FROM entry_table")
    suspend fun deleteAll()
}
