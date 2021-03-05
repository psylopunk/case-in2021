package iooojik.casein.ui.beginner.chatbot

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.chatbot.ChatBotMessage
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.request.SendMessageModel
import iooojik.casein.web.models.response.MessagesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


@Suppress("DEPRECATION")
class BotFragment : Fragment(), View.OnClickListener {

    private lateinit var rootView : View
    private lateinit var buttonsField : LinearLayout
    private val netModel = NetModel()
    private lateinit var preferences: SharedPreferences
    private lateinit var token : String
    private lateinit var buttons : List<String>
    private var messages : ArrayList<MessagesResponse> = arrayListOf()
    private val handler = Handler()
    private lateinit var database: AppDatabase
    private lateinit var botChatAdapter: BotChatAdapter
    private var running = true
    private var startUp = true

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_bot_chat, container, false)
        initialize()
        return rootView
    }

    private fun initialize(){
        buttonsField = rootView.findViewById(R.id.buttons_field)

        rootView.findViewById<ImageView>(R.id.button_back).setOnClickListener(this)
        rootView.findViewById<TextView>(R.id.user_name_text_view).text = "Джарвис"
        rootView.findViewById<ImageView>(R.id.avatar).setImageResource(R.drawable.baseline_smart_toy_24)
        rootView.findViewById<ImageView>(R.id.send_message_button).setOnClickListener(this)
        rootView.findViewById<ImageView>(R.id.show_keyboard).visibility = View.VISIBLE
        rootView.findViewById<ImageView>(R.id.show_keyboard).setOnClickListener(this)

        requireActivity().findViewById<FloatingActionButton>(R.id.fab).hide()
        database = AppDatabase.getAppDataBase(requireContext())!!
        database.chatBotMessageDao().deleteAll()

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).visibility = View.GONE

        val recQuestChat = rootView.findViewById<RecyclerView>(R.id.bot_chat)
        recQuestChat.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, true)

        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()

        threadGetMessages()

        handler.post(object : Runnable {
            override fun run() {
                if (running){
                    threadGetMessages()
                    handler.postDelayed(this, 3000)
                }
            }
        })
    }

    private fun threadGetMessages(){
        thread {
            getMessages()
        }
    }

    private fun sendMessage(sendMessageModel: SendMessageModel){
        netModel.botApi.sendMessage(sendMessageModel, token).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                threadGetMessages()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    private fun getMessages() {

        netModel.botApi.getMessages("0", "1000", token).enqueue(object : Callback<List<MessagesResponse>> {
            override fun onResponse(call: Call<List<MessagesResponse>>, response: Response<List<MessagesResponse>>) {
                if (response.isSuccessful) {
                    val messages = response.body()
                    if (messages != null) requireActivity().runOnUiThread {  saveMessages(messages) }
                }
                showStartKeyBoard(startUp)
            }

            override fun onFailure(call: Call<List<MessagesResponse>>, t: Throwable) {
                t.printStackTrace()
            }

        })

    }

    private fun saveMessages(list: List<MessagesResponse>) {

        list.forEach {
            val md = ChatBotMessage(null, it.message, it.incoming, it.created)
            database.chatBotMessageDao().insert(md)
        }

        if (messages.size != list.size){
            messages = list as ArrayList<MessagesResponse>
            botChatAdapter = BotChatAdapter(requireContext(), requireActivity(), messages)

            val recMessages = rootView.findViewById<RecyclerView>(R.id.bot_chat)
            recMessages.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, true)
            recMessages.adapter = botChatAdapter
        }

        if (list.isNotEmpty()) {
            val keyboard = list[0].keyboard
            if (keyboard != null) {
                addKeyBoard(keyboard)
            } else buttonsField.removeAllViews()
        }
    }

    private fun showStartKeyBoard(start : Boolean){
        if (messages.size == 0 && start){
            startUp = false
            addKeyBoard(mutableListOf("/start"))
        }
    }

    private fun addKeyBoard(keyboard: MutableList<String>) {
        buttonsField.removeAllViews()
        keyboard.forEachIndexed { _, s ->
            val layout = requireActivity().layoutInflater.inflate(R.layout.button, null)
            val button = layout.findViewById<Button>(R.id.button)
            button.text = s
            button.setOnClickListener {
                val model = SendMessageModel()
                model.message = s
                sendMessage(model)
            }
            buttonsField.addView(layout)
        }
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_back -> {
                requireActivity().findNavController(R.id.nav_host_fragment).navigateUp()
            }
            R.id.send_message_button -> {

                val model = SendMessageModel()
                val messageText = rootView.findViewById<EditText>(R.id.message_text_field)

                if (!messageText.text.isNullOrEmpty()) {
                    model.message = messageText.text.toString()
                    sendMessage(model)
                }

                messageText.text.clear()
                messageText.clearFocus()

            }

            R.id.show_keyboard -> {
                if (buttonsField.visibility == View.VISIBLE)
                    buttonsField.visibility = View.GONE
                else buttonsField.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        running = false
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).visibility = View.VISIBLE
    }
}