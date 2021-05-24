package pt.rfernandes.loopuiux.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *   Class TravelEntry created at 5/18/21 22:36 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
@Entity(tableName = "entry_table")
class TravelEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String
) {
    var isNewEntry: Boolean = true
    var isDetailsOpen: Boolean = false
    var isDeleteOpen: Boolean = false
    var alreadySeen: Boolean = false
}
