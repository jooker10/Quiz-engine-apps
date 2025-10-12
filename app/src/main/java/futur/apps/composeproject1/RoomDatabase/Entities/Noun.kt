package futur.apps.composeproject1.RoomDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a noun entity in the database.
 *
 * @property id The unique identifier of the noun.
 * @property en The English translation of the noun.
 * @property fr The French translation of the noun.
 * @property sp The Spanish translation of the noun.
 * @property ar The Arabic translation of the noun.
 * @property ex An example sentence or usage of the noun.
 */
@Entity(tableName = "nouns")
data class Noun(
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
    override var ar : String,
    @ColumnInfo(name = "ex")
    val ex : String
    ) : futur.apps.composeproject1.utils.DataEntity