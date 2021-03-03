package iooojik.casein.ui.curator.chat_rooms

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.chatRooms.ChatRoomModel

class ChatRoomsAdapter(
    context: Context, activity: Activity, rooms : List<ChatRoomModel>
) : RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder>() {

    private val context = context
    private val inflater = activity.layoutInflater
    private val rooms = rooms
    private val activity = activity

    private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_child_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatRoomModel = rooms[position]
        holder.senderToNameField.text = chatRoomModel.roomName
        holder.avatar.setImageResource(R.drawable.baseline_account_circle_24)
        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putString("chatId", chatRoomModel.roomUniqueID)
            args.putString("sendTO", chatRoomModel.roomName)
            activity.findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_chat_room, args)
        }
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val senderToNameField : TextView = itemView.findViewById(R.id.name)
        val avatar : ImageView = itemView.findViewById(R.id.avatar)
    }

}