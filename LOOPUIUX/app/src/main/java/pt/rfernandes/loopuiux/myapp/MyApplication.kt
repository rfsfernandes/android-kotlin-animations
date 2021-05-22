package pt.rfernandes.loopuiux.myapp

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

/**
 *   Class MyApplication created at 5/21/21 23:54 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this);
    }
}
