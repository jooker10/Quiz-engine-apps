package futur.apps.composeproject1.RoomDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import futur.apps.composeproject1.utils.Table

/**
 * Represents a verb entity in the database.
 *
 * @property id The unique identifier of the verb.
 * @property en The English translation of the verb.
 * @property fr The French translation of the verb.
 * @property sp The Spanish translation of the verb.
 * @property ar The Arabic translation of the verb.
 * @property ex An example sentence or usage of the verb.
 */
@Entity(tableName = "verbs")
data class Verb(
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
    ) : Table