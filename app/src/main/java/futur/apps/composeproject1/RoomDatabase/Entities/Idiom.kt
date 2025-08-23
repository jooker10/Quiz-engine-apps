package futur.apps.composeproject1.RoomDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import futur.apps.composeproject1.utils.Table

/**
 * Represents an idiom entity in the database.
 *
 * @property id The unique identifier of the idiom.
 * @property en The English representation of the idiom.
 * @property fr The French translation of the idiom.
 * @property sp The Spanish translation of the idiom.
 * @property ar The Arabic translation of the idiom.
 */
@Entity(tableName = "idioms")
data class Idiom(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override var id : Int,
    @ColumnInfo(name = "en")
    override var en : String,
    @ColumnInfo(name = "fr")
    override var fr : String,
    @ColumnInfo(name = "sp")
    override var sp: String,
    @ColumnInfo(name = "ar")
    override var ar : String
    /*@ColumnInfo(name = "ex")
    val ex : String*/
    ) : Table