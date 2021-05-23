package pt.rfernandes.loopuiux.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 *   Class PersistentDatabaseCallback created at 5/23/21 16:26 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class PersistentDatabaseCallback(
    private val scope: CoroutineScope,
    private var INSTANCE: PersistentDatabase?
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
            scope.launch {
                populateDatabase(database.entryDao())
            }
        }
    }

    suspend fun populateDatabase(wordDao: EntryDao) {
        // Delete all content here.
        wordDao.deleteAll()

        // Add sample words.

    }
}
