package iooojik.casein.web.models

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageModel(message : String, uniqueRoomID : String, sender : String) {
    @SerializedName("message")
    @Expose
    var message: String? = message

    @SerializedName("unique_room_id")
    @Expose
    var uniqueRoomID: String? = uniqueRoomID

    @SerializedName("sender")
    @Expose
    var sender: String? = sender

    fun toJson() : String{
        return Gson().toJson(this)
    }
}