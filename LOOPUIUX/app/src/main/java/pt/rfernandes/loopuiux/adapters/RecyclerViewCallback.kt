package pt.rfernandes.loopuiux.adapters

import pt.rfernandes.loopuiux.model.TravelEntry

interface RecyclerViewCallback {
    fun clickedItem(position: Int)
    fun deletedItem(travelEntry: TravelEntry)
}