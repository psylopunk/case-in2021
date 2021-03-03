package iooojik.casein.localData.chatbot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ChatBotMessage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,

    @ColumnInfo(name = "message")
    val message: String? = null,

    @ColumnInfo(name = "incoming")
    val incoming: Boolean? = null,

    @ColumnInfo(name = "created")
    val created: String? = null
)