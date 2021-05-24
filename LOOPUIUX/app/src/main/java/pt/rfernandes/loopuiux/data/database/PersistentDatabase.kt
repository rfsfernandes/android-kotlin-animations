package pt.rfernandes.loopuiux.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.model.TravelEntry

/**
 *   Class PersistentDatabase created at 5/23/21 16:11 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */

@Database(entities = [TravelEntry::class], version = 1, exportSchema = false)
abstract class PersistentDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    class PersistentDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        private val imageNames: Array<Int> = arrayOf(
            R.mipmap.building,
            R.mipmap.mountain,
            R.mipmap.sky,
            R.mipmap.city,
            R.mipmap.mountain_view
        )

        private val titleArray: Array<String> =
            arrayOf("Title 1", "Title2", "Title3", "Title 4", "Title 5")

        private val contentArray: Array<String> =
            arrayOf("Content 1", "Content 2", "Content 3", "Content 4", "Content 5")

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.entryDao())
                }
            }
        }

        private suspend fun populateDatabase(entryDao: EntryDao) {

            entryDao.deleteAll()

            for (i in titleArray.indices) {
                val temp = TravelEntry(0, imageNames[i].toString(), titleArray[i], contentArray[i])
                entryDao.insertEntry(temp)
            }

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
                    .addCallback(PersistentDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}