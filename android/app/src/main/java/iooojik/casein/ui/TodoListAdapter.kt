package iooojik.casein.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.ui.curator.bottomsheets.BottomSheetUserInfo
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.request.EditTaskRequest
import iooojik.casein.web.models.response.tasks.TasksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class TodoListAdapter(
    private val context: Context,
    private val activity: Activity,
    private val todos: List<TasksResponse>,
    private val fragment : BottomSheetUserInfo?
) : RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    private val inflater = activity.layoutInflater
    private val netModel = NetModel()
    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_todo_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //получение модельки таска
        val model = todos[position]

        //название таска
        holder.checkBox.text = model.title

        //проверяем, выполнено ли оно
        if (model.status.equals("completed")) {
            blockCheckBox(holder.checkBox)
        }

        //"вес" задания
        holder.textScore.text = (model.difficulty*10).toInt().toString()

        //изменение статуса таска
        holder.checkBox.setOnCheckedChangeListener { _ , isChecked ->

            if (isChecked) model.status = "completed"
            else model.status = "created"

            changeTaskStatus(model)
            blockCheckBox(holder.checkBox)

            thread {
                Log.e("starts", "eee")
                activity.runOnUiThread {
                    fragment?.getInfoForGraph()
                }

            }

        }
        holder.description.text = model.description.toString()

    }

    private fun blockCheckBox(checkBox : CheckBox){
        checkBox.isChecked = true
        checkBox.isClickable = false
        checkBox.isFocusable = false
    }

    private fun changeTaskStatus(model: TasksResponse) {
        val md = EditTaskRequest()
        md.id = model.id
        md.title = model.title
        md.status = model.status

        netModel.tasksApi.editTask(md, preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString())
            .enqueue(object : Callback<TasksResponse> {
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {

                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checkBox = itemView.findViewById<CheckBox>(R.id.item_check)
        val textScore = itemView.findViewById<TextView>(R.id.text_score)
        val description = itemView.findViewById<TextView>(R.id.description)
    }
}