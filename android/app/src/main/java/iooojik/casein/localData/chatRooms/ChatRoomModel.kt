package iooojik.casein.localData.chatRooms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ChatRoomModel(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id : Long? = null,

        @ColumnInfo(name = "room_name")
        var roomName : String,

        @ColumnInfo(name = "room_unique_id")
        var roomUniqueID : String

)