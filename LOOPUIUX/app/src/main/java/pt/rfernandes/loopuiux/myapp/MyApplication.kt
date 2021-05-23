package pt.rfernandes.loopuiux.myapp

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import pt.rfernandes.loopuiux.data.DataRepository
import pt.rfernandes.loopuiux.data.database.PersistentDatabase

/**
 *   Class MyApplication created at 5/21/21 23:54 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class MyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { PersistentDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { DataRepository(database.entryDao()) }

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this);
    }
}
