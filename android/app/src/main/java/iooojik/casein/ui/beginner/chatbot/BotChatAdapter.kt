package iooojik.casein.ui.beginner.chatbot

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.web.models.response.MessagesResponse
import iooojik.casein.web.models.response.tasks.TasksResponse

class BotChatAdapter (

    private val context: Context,
    private val activity: Activity,
    private val items: List<MessagesResponse>

) : RecyclerView.Adapter<BotChatAdapter.ViewHolder>() {

    private val inflater = activity.layoutInflater
    private val chatBotName = "Джарвис"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_message_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]
        val view : View

        if (!model.incoming){
            view = inflater.inflate(R.layout.incoming_message, null)
            view.findViewById<TextView>(R.id.name).text = chatBotName
            val messageTextView = view.findViewById<TextView>(R.id.message_text)
            messageTextView.text = model.message
        } else {
            view = inflater.inflate(R.layout.my_message_item, null)
            val messageTextView = view.findViewById<TextView>(R.id.message_text)
            messageTextView.text = model.message
        }

        holder.messageLayout.addView(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val messageLayout: LinearLayout = itemView.findViewById(R.id.message_field)
    }

}