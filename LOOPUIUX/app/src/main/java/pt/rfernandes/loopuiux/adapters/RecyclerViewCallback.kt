package pt.rfernandes.loopuiux.adapters

import pt.rfernandes.loopuiux.model.TravelEntry

interface RecyclerViewCallback {
    fun deletedItem(travelEntry: TravelEntry)
}