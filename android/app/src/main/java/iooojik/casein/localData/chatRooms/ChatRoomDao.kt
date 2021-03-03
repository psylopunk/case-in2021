package iooojik.casein.localData.chatRooms

import androidx.room.*

@Dao
interface ChatRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatRoomModel: ChatRoomModel)

    @Update
    fun update(chatRoomModel: ChatRoomModel)

    @Delete
    fun delete(chatRoomModel: ChatRoomModel)

    @Query("DELETE FROM chatroommodel WHERE room_unique_id = :uniqueID")
    fun deleteByUniqueID(uniqueID : String)

    @Query("SELECT * FROM chatroommodel")
    fun getAll() : List<ChatRoomModel>

    @Query("SELECT * FROM chatroommodel ORDER BY _id DESC LIMIT 1")
    fun getLastRoom() : ChatRoomModel

    @Query("DELETE FROM chatroommodel")
    fun deleteAll()

    @Query("SELECT * FROM chatroommodel WHERE _id = :id")
    fun getById(id : Long) : ChatRoomModel

    @Query("SELECT * FROM chatroommodel WHERE room_unique_id = :uniqueID")
    fun getByUniqueID(uniqueID : String) : ChatRoomModel

    @Query("SELECT * FROM chatroommodel WHERE room_name = :roomName")
    fun getByRoomName(roomName : String) : ChatRoomModel

}
