package iooojik.casein.ui.curator.bottomsheets

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.bottomsheet.BottomSheetDialog
import iooojik.casein.LogMessages
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.ui.TodoListAdapter
import iooojik.casein.ui.curator.CuratorHomeFragment
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.response.tasks.TasksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.ArrayList
import kotlin.concurrent.thread

class BottomSheetUserInfo (
    private val context: Context,
    private val activity: Activity,
    private val model: ChildModel,
    private val fragment: CuratorHomeFragment
) : View.OnClickListener{

    private val logMessages = LogMessages()
    private val bottomView : View = activity.layoutInflater.inflate(R.layout.bottom_sheet_user_info, null)
    val bottomSheetDialog : BottomSheetDialog = BottomSheetDialog(context)
    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
    private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!
    private lateinit var todoListAdapter: TodoListAdapter
    private val netModel = NetModel()
    private lateinit var token : String

    init {
        initializeBottomSheet()
        bottomSheetDialog.setContentView(bottomView)
        Log.i(logMessages.SYSTEM_MESSAGE, "BOTTOM SHEET CREATED")

        thread {
            updateData()
        }

    }

    fun updateData(){
        getTasks()
        getInfoForGraph()
    }

    fun getTasks() {
        //получение заданий

        //токен
        token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()

        //запрос
        netModel.tasksApi.getTasks(token).enqueue(object : Callback<List<TasksResponse>>{

            override fun onResponse(call: Call<List<TasksResponse>>, response: Response<List<TasksResponse>>) {
                if (response.isSuccessful){
                    try {

                        //получаем ответ и получаем задания пользователя
                        val body = response.body()!!

                        val list : MutableList<TasksResponse> = mutableListOf()

                        for (b in body){
                            if (b.employee.id.equals(model.modelId)) list.add(b)
                        }


                        if (list.isNotEmpty()) activity.runOnUiThread {
                            showTasks(list)
                        }

                    } catch (e : Exception){
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailure(call: Call<List<TasksResponse>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun showTasks(tasks: List<TasksResponse>){
        val recTasks = bottomView.findViewById<RecyclerView>(R.id.rec_tasks_list)
        recTasks.layoutManager = LinearLayoutManager(context)
        todoListAdapter = TodoListAdapter(context, activity, tasks, this)
        recTasks.adapter = todoListAdapter
    }

    private fun initializeBottomSheet() {
        bottomView.findViewById<TextView>(R.id.user_name_text_view).text = model.fullName
        bottomView.findViewById<ImageView>(R.id.go_to_chat).setOnClickListener(this)
        bottomView.findViewById<Button>(R.id.add_task_button).setOnClickListener(this)
    }

    fun getInfoForGraph(){
        Log.e("starts", "from method")
        netModel.tasksApi.getCompletedTasks("completed", model.modelId!!, token)
            .enqueue(object : Callback<List<TasksResponse>>{

            override fun onResponse(
                call: Call<List<TasksResponse>>,
                response: Response<List<TasksResponse>>
            ) {
                if (response.isSuccessful){

                    if (response.body() != null){

                        val resp = response.body()!!.asReversed()

                        if (resp.isNotEmpty()){
                            activity.runOnUiThread {
                                drawGraph(resp)
                            }
                        }

                    }
                }
            }

            override fun onFailure(call: Call<List<TasksResponse>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun drawGraph(resp: List<TasksResponse>) {
        val values : ArrayList<Entry> = arrayListOf()

        for (i in 0..resp.size - 1){
            values.add(Entry(
                i.toFloat(),
                (resp[i].difficulty*10).toInt().toFloat()
            ))
        }

        if (values.size > 0){
            val chart = bottomView.findViewById<LineChart>(R.id.chart)
            chart.invalidate()

            chart.setPinchZoom(false)
            chart.isDoubleTapToZoomEnabled = false
            chart.description.isEnabled = false

            chart.legend.isEnabled = false
            chart.legend.textColor = Color.parseColor("#ffcf44")
            chart.legend.textSize = 14f

            chart.xAxis.textColor = Color.WHITE

            chart.axisLeft.textColor = Color.WHITE
            chart.axisLeft.isEnabled = false

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.WHITE


            val lineDataSet = LineDataSet(values, "Зависимость количества выполненных задач от их сложности")

            lineDataSet.valueTextColor = Color.parseColor("#ffcf44")
            lineDataSet.disableDashedLine()
            lineDataSet.lineWidth = 3f
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = Color.parseColor("#B15DFF")
            lineDataSet.setDrawHorizontalHighlightIndicator(false)

            val dataSets : ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(lineDataSet)

            chart.data = LineData(dataSets)
            chart.visibility = View.VISIBLE
        }
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_to_chat -> {
                //переход на страницу с чатом
                val args = Bundle()
                args.putString("chatId", model.modelId)
                args.putString("sendTO", model.fullName)
                activity.findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_chat_room, args)
                bottomSheetDialog.hide()
            }

            R.id.add_task_button -> {
                //кнопка добавления задания
                val addTask = BottomSheetAddTask(
                    context,
                    activity,
                    model.modelId.toString(),
                    fragment,
                    this,
                    bottomView)
                    .bottomSheetDialog
                addTask.show()
            }
        }
    }

    /*
    class TodoListAdapter(
        private val context: Context,
        private val activity: Activity,
        private val todos : List<TasksResponse>,
        private val bottomSheetUserInfo: BottomSheetUserInfo

    ) : RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

        private val inflater = activity.layoutInflater
        private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!
        private val netModel = NetModel()
        private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.recycler_view_todo_item, parent, false))
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val model = todos[position]
            holder.checkBox.text = model.title

            if (model.status.equals("completed")) {
                holder.checkBox.isChecked = true
                holder.checkBox.isClickable = false
                holder.checkBox.isFocusable = false
            }

            holder.textScore.text = (model.difficulty*10).toInt().toString()
            holder.description.text = model.description.toString()

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) model.status = "completed"
                else model.status = "rejected"
                changeTaskStatus(model)
            }
        }

        private fun changeTaskStatus(model: TasksResponse) {
            val md = EditTaskRequest()
            md.id = model.id
            md.title = model.title
            md.status = model.status

            netModel.tasksApi.editTask(md, preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString())
                    .enqueue(object : Callback<TasksResponse>{
                        override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                            if (!response.isSuccessful) Log.e("error", response.raw().toString())
                            bottomSheetUserInfo.getInfoForGraph()
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

     */

}