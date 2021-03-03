package iooojik.casein.localData.childs

import androidx.room.*
import iooojik.casein.localData.chatRooms.ChatRoomModel

@Dao
interface ChildModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(childModel: ChildModel)

    @Update
    fun update(childModel: ChildModel)

    @Delete
    fun delete(childModel: ChildModel)

    @Query("SELECT * FROM childmodel WHERE model_id = :id")
    fun getByModelID(id : String) : ChildModel

    @Query("SELECT * FROM childmodel")
    fun getAll() : List<ChildModel>

    @Query("DELETE FROM childmodel")
    fun deleteAll()
}