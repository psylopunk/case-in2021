package iooojik.casein.ui.chat_room

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.StaticVars
import org.json.JSONObject

class ChatListAdapter(private val messages: List<JSONObject>, private val activity: Activity)
    : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
    //id текущего пользователя
    private val myNickname = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_NICKNAME, "")
    private val inflater = activity.layoutInflater



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_message_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //получаем модель сообщения
        val message = messages[position]
        val inflater = activity.layoutInflater
        val view : View

        //если текущий пользователь отправитель
        if (message.getString("sender") == myNickname){
            //то используем модель my_message_item для отображения в ListView
            view = inflater.inflate(R.layout.my_message_item, null)
            val messageTextView = view.findViewById<TextView>(R.id.message_text)
            messageTextView.text = message.getString("message")

        } else {

            //иначе используем incoming_message
            view = inflater.inflate(R.layout.incoming_message, null)
            val messageTextView = view.findViewById<TextView>(R.id.message_text)
            messageTextView.text = message.getString("message")
            val senderName = view.findViewById<TextView>(R.id.name)
            senderName.text = message.getString("sender")
            val avatar = view.findViewById<View>(R.id.avatar)

            /*
            if (position - 1 >= 0){
                val message1 = messages[position - 1]
                if (message1.getString("sender") == message.getString("sender")){
                    senderName.visibility = View.GONE
                    avatar.visibility = View.INVISIBLE
                }
            }

             */

        }
        holder.messageLayout.addView(view)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val messageLayout: LinearLayout = itemView.findViewById(R.id.message_field)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}