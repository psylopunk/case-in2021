package iooojik.casein.web.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class RoomModel {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("roomUniqueID")
    @Expose
    var roomUniqueID: String? = null

    @SerializedName("user1")
    @Expose
    var user1: String? = null

    @SerializedName("user2")
    @Expose
    var user2: String? = null

}