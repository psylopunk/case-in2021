package iooojik.casein.ui.beginner.rating

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.ui.beginner.chatbot.BotChatAdapter
import iooojik.casein.web.models.response.MessagesResponse
import iooojik.casein.web.models.response.Scoreboard

class RatingAdapter (

    private val context: Context,
    private val activity: Activity,
    private val items: List<Scoreboard>,
    private val userPos : Int,
    private val userName : String

) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    private val inflater = activity.layoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.recycler_rating_item,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]
        holder.avatar.setImageResource(R.drawable.baseline_account_circle_24)
        holder.rating.text = (model.score * 100).toInt().toString()
        holder.userName.text = model.fullName
        if (userName == model.login){
            holder.itemView.setBackgroundColor(Color.parseColor("#1eb980"))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val userName : TextView = itemView.findViewById(R.id.user_name_text_view)
        val rating : TextView = itemView.findViewById(R.id.rating)
    }

}