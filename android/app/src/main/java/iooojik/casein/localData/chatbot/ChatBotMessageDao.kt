package iooojik.casein.localData.chatbot

import androidx.room.*
import iooojik.casein.localData.childs.ChildModel

@Dao
interface ChatBotMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatBotMessage: ChatBotMessage)

    @Update
    fun update(chatBotMessage: ChatBotMessage)

    @Delete
    fun delete(chatBotMessage: ChatBotMessage)

    @Query("SELECT * FROM chatbotmessage")
    fun getAll() : List<ChatBotMessage>

    @Query("DELETE FROM chatbotmessage")
    fun deleteAll()
}