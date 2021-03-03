package iooojik.casein.ui.curator.bottomsheets

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import iooojik.casein.MainActivity
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.ui.curator.CuratorHomeFragment
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.request.AddTaskRequest
import iooojik.casein.web.models.response.tasks.TasksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class BottomSheetAddTask(
    private val context: Context,
    private val activity: Activity,
    private val userID: String,
    private val fragment: CuratorHomeFragment,
    private val parentBottomSheet : BottomSheetUserInfo,
    private val view: View
) : View.OnClickListener{

    private val bottomView : View = activity.layoutInflater.inflate(R.layout.bottom_sheet_add_task, null)
    val bottomSheetDialog : BottomSheetDialog = BottomSheetDialog(context)
    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
    private val netModel = NetModel()
    private val token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()
    private val mainActivity = MainActivity()

    init {
        initialize()
        bottomSheetDialog.setContentView(bottomView)
        Log.e("ttt", token)

    }

    private fun initialize(){
        bottomView.findViewById<Button>(R.id.add_button).setOnClickListener(this)
    }

    private fun addTask(){
        val taskTitle = bottomView.findViewById<EditText>(R.id.task_title).text.toString()
        val taskDescription = bottomView.findViewById<EditText>(R.id.task_description).text.toString()
        var taskDif = (bottomView.findViewById<Slider>(R.id.difficulty_slider).value / 10f)

        if (taskDif < 0.1f){
            taskDif = 0.5f
        }

        if (taskTitle.isEmpty() || taskDescription.isEmpty()){
            activity.runOnUiThread {
                Toast.makeText(context, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
            }
        } else {
            val taskRequestModel = AddTaskRequest()
            taskRequestModel.userId = userID
            taskRequestModel.title = taskTitle
            taskRequestModel.description = taskDescription
            taskRequestModel.difficulty = taskDif.toDouble()

            addTaskRequest(taskRequestModel)
        }

    }

    private fun addTaskRequest(taskRequestModel: AddTaskRequest) {
        netModel.tasksApi.addTask(taskRequestModel, token).enqueue(object : Callback<TasksResponse> {

            override fun onResponse(
                call: Call<TasksResponse>,
                response: Response<TasksResponse>
            ) {
                if (response.isSuccessful) {
                    mainActivity.showSnackbar("Добавлено", view)
                    parentBottomSheet.updateData()
                    bottomSheetDialog.hide()
                } else {
                    mainActivity.showSnackbar("Не удалось добавить задание", bottomView)
                }
            }

            override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                mainActivity.showSnackbar("Не удалось добавить задание", view)
                Log.e("childApi error", t.toString())
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.add_button -> {
                thread {
                    addTask()
                    activity.runOnUiThread {
                        parentBottomSheet.getTasks()
                    }
                }

            }
        }
    }
}