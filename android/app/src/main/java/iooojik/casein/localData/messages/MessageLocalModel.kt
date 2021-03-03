package iooojik.casein.localData.messages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MessageLocalModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "message")
    var message : String,

    @ColumnInfo(name = "room_unique_id")
    var roomUniqueID : String,

    @ColumnInfo(name = "sender")
    var sender : String
)