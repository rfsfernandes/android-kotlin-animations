package pt.rfernandes.loopuiux.model

/**
 *   Class EntryContent created at 5/18/21 22:36 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class EntryContent(val image: String, val title: String, val content: String) {
    var isOpen: Boolean = false
    var hasBeenLoaded: Boolean = false
    var alreadySeen: Boolean = false
}
