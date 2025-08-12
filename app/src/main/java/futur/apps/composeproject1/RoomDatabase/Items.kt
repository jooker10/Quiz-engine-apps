package futur.apps.composeproject1.RoomDatabase

import kotlinx.coroutines.flow.Flow

interface Items {
    var id : Int
    var en : String
    var fr : String
    var sp : String
    var ar : String

    //fun getAll(): Flow<Items>
}