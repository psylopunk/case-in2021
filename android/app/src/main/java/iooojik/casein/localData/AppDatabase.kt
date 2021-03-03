package iooojik.casein.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import iooojik.casein.localData.chatRooms.ChatRoomDao
import iooojik.casein.localData.chatRooms.ChatRoomModel
import iooojik.casein.localData.chatbot.ChatBotMessage
import iooojik.casein.localData.chatbot.ChatBotMessageDao
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.localData.childs.ChildModelDao
import iooojik.casein.localData.messages.MessageDao
import iooojik.casein.localData.messages.MessageLocalModel

@Database(entities = [ChatRoomModel::class, MessageLocalModel::class,
    ChildModel::class, ChatBotMessage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatRoomDao() : ChatRoomDao
    abstract fun messageDao() : MessageDao
    abstract fun childModelDao() : ChildModelDao
    abstract fun chatBotMessageDao() : ChatBotMessageDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){

                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "database").allowMainThreadQueries().build()

                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}