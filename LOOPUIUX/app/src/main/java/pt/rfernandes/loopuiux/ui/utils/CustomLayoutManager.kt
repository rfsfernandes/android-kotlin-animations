package pt.rfernandes.loopuiux.ui.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 *   Class CustomLayoutManager created at 5/23/21 22:22 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class CustomLayoutManager(context: Context) : LinearLayoutManager(context) {

    private var isScrollEnabled: Boolean = true

    fun setScrollEnabled(canScroll: Boolean) = run { isScrollEnabled = canScroll }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }

}
