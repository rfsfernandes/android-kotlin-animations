package pt.rfernandes.loopuiux.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import pt.rfernandes.loopuiux.data.database.EntryDao
import pt.rfernandes.loopuiux.model.TravelEntry

/**
 *   Class DataRepository created at 5/23/21 16:14 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class DataRepository(private val entryDao: EntryDao) {

    val getTravelEntries: Flow<List<TravelEntry>> = entryDao.getEntries()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(entry: TravelEntry) {
        entryDao.insertEntry(entry)
    }

    @WorkerThread
    suspend fun delete(entry: TravelEntry) {
        entryDao.deleteEntry(entry.id)
    }

}
