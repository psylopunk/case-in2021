package iooojik.casein.ui.curator

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.ui.curator.bottomsheets.BottomSheetDeleteChild
import iooojik.casein.ui.curator.bottomsheets.BottomSheetUserInfo

class ChildsAdapter(
    private val context: Context, private val activity: Activity,
    private val childs: List<ChildModel>, private val fragment: CuratorHomeFragment
) : RecyclerView.Adapter<ChildsAdapter.ViewHolder>() {

    private val inflater = activity.layoutInflater
    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
    private val accountType = preferences.getInt(StaticVars().PREFERENCES_CURRENT_USER_ACCOUNT_TYPE, 2)
    private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_child_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val childModel = childs[position]
        holder.avatar.setImageResource(R.drawable.baseline_account_circle_24)
        holder.nameTextView.text = childModel.fullName

        if (accountType != 0)
            holder.itemView.setOnClickListener {
                BottomSheetUserInfo(context, activity, childModel, fragment)
                    .bottomSheetDialog
                    .show()
            }

        holder.itemView.setOnLongClickListener {
            val btmDelete = BottomSheetDeleteChild(context, activity, childModel, fragment).bottomSheetDialog
            btmDelete.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return childs.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView : TextView = itemView.findViewById(R.id.name)
        val avatar : ImageView = itemView.findViewById(R.id.avatar)
    }

}