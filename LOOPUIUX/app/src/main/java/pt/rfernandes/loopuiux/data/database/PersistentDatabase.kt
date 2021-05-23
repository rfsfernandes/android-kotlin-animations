package pt.rfernandes.loopuiux.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pt.rfernandes.loopuiux.model.TravelEntry

/**
 *   Class PersistentDatabase created at 5/23/21 16:11 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */

@Database(entities = [TravelEntry::class], version = 1, exportSchema = false)
public abstract class PersistentDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao

    private class PersistentDatabaseCallback(
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

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PersistentDatabase? = null


        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PersistentDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PersistentDatabase::class.java,
                    "entry_database"
                )
                    .addCallback(PersistentDatabaseCallback(scope, INSTANCE))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}