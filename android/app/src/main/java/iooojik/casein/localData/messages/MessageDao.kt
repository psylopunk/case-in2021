package iooojik.casein.localData.messages

import androidx.room.*

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageLocalModel: MessageLocalModel)

    @Update
    fun update(messageLocalModel: MessageLocalModel)

    @Delete
    fun delete(messageLocalModel: MessageLocalModel)

    @Query("SELECT * FROM messagelocalmodel")
    fun getAll() : List<MessageLocalModel>

    @Query("SELECT * FROM messagelocalmodel WHERE room_unique_id = :uniqueID")
    fun getAllByUniqueID(uniqueID : String) : List<MessageLocalModel>

}