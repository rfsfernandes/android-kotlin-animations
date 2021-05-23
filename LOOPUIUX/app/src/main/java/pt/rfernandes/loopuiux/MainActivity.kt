package pt.rfernandes.loopuiux

import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import com.google.android.material.appbar.AppBarLayout
import pt.rfernandes.loopuiux.ui.utils.ToolbarCustomBehaviour

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: AppBarLayout = findViewById(R.id.appbar)
        val drawerIcon : View = findViewById(R.id.drawer_icon)
        (toolbar.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarCustomBehaviour()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}