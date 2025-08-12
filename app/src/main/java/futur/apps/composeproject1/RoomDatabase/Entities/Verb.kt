package futur.apps.composeproject1.RoomDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import futur.apps.composeproject1.RoomDatabase.Items


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
    ) : Items