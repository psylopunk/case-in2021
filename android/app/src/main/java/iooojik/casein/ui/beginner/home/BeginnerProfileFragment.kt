package iooojik.casein.ui.beginner.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.ui.TodoListAdapter
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.response.tasks.TasksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BeginnerProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var database: AppDatabase
    private lateinit var preferences: SharedPreferences
    private val netModel = NetModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_beginner_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initViews(){
        //кнопка перехода на чат с куратором
        val goToChat = requireView().findViewById<Button>(R.id.go_to_chat)
        goToChat.setOnClickListener(this)
    }

    private fun initialization() {
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        database = AppDatabase.getAppDataBase(requireContext())!!

        requireActivity().findViewById<FloatingActionButton>(R.id.fab).hide()

        showInformation()
        initViews()
    }

    private fun showInformation() {
        //показываем информацию в ui
        getTasks()
        //показываем фио пользователя
        setUserName()
    }

    private fun setUserName() {
        val userNameField = requireView().findViewById<TextView>(R.id.user_name_text_view)
        userNameField.text = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_NAME, "").toString()
    }

    private fun setTodos(list : List<TasksResponse>) {
        //показываем tasks-лист
        todoListAdapter = TodoListAdapter(requireContext(), requireActivity(), list, null)
        val recViewTodos = requireView().findViewById<RecyclerView>(R.id.rec_tasks_list)
        recViewTodos.layoutManager = LinearLayoutManager(requireContext())
        recViewTodos.adapter = todoListAdapter
    }

    private fun getTasks() {
        //получение тасков
        val token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()

        //запрос
        netModel.tasksApi.getTasks(token).enqueue(object : Callback<List<TasksResponse>> {

            override fun onResponse(call: Call<List<TasksResponse>>, response: Response<List<TasksResponse>>) {
                if (response.isSuccessful){
                    try {
                        //отображение полученных тасков
                        response.body()?.let { setTodos(it) }
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

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_to_chat -> {
                //переход в комнату с чатом
                val args = Bundle()
                //уникальный id чата - id текущего пользователя
                args.putString("chatId", preferences.getString(StaticVars().PREFERENCES_USER_ID, ""))
                //имя отправителя(для отображения в toolbar)
                args.putString("sendTO", preferences.getString(StaticVars().PREFERENCES_PARENT_NAME, ""))
                //переход на страницу с чатом
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_chat_room, args)
            }
        }
    }

}