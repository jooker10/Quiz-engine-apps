package futur.apps.composeproject1.RoomDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an adverb entity in the database.
 *
 * @property id The unique identifier of the adverb.
 * @property en The English representation of the adverb.
 * @property fr The French translation of the adverb.
 * @property sp The Spanish translation of the adverb.
 * @property ar The Arabic translation of the adverb.
 * @property ex An example sentence or usage of the adverb.
 */
@Entity(tableName = "adverbs")
data class Adverb(
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