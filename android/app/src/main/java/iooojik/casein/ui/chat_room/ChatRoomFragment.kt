package iooojik.casein.ui.chat_room

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import iooojik.casein.R
import iooojik.casein.SocketEvents
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.chatRooms.ChatRoomDao
import iooojik.casein.localData.messages.MessageLocalModel
import iooojik.casein.web.models.MessageModel
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.concurrent.thread


class ChatRoomFragment : Fragment(), View.OnClickListener {

    private lateinit var database: AppDatabase
    private lateinit var roomDao: ChatRoomDao
    private lateinit var mSocket : Socket
    private lateinit var messagesList : ArrayList<JSONObject>
    private var uniqueRoomID = ""
    private lateinit var preferences : SharedPreferences
    private lateinit var myNickname : String
    private lateinit var messagesListAdapter: ChatListAdapter
    private val socketEvents = SocketEvents()
    private lateinit var messageList : RecyclerView
    private lateinit var rootView : View



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_chat_room, container, false)
        initialize()
        return rootView
    }

    private fun loadSocketConfiguration(){
        //открываем сокет-соединение
        try {
            mSocket = IO.socket(StaticVars().SOCKET_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        mSocket.open()
        mSocket.emit(socketEvents.EVENT_JOIN, uniqueRoomID)
        mSocket.on(socketEvents.EVENT_CHAT_MESSAGE, onNewMessage)
        mSocket.on(socketEvents.EVENT_NOTIFICATION, Emitter.Listener {
            requireActivity().runOnUiThread {
                //setMessages()
                val message = JSONObject(it[0].toString())
                messagesList.reverse()
                messagesList.add(message)
                messagesList.reverse()
                messagesListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initialize(){
        //получение уникального Id комнаты
        uniqueRoomID = getChatRoomID()
        thread {
            //открываем сокет-соединение
            loadSocketConfiguration()
        }
        //обновляем ui
        initViews()
    }

    private fun getChatRoomID(): String {
        //получение уникального ид для чата
        val args = requireArguments()
        return args.getString("chatId").toString()
    }

    private fun initViews() {
        //показываем вьюшки и записываем данные в переменные
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).visibility = View.GONE

        //обновляем переменные
        setVars()

        //скрываем fab
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.hide()

        //кнопка отправки сообщения
        val sendMessageButton = rootView.findViewById<ImageView>(R.id.send_message_button)
        sendMessageButton.setOnClickListener(this)

        //кнопка "назад" в тулбаре
        val goBackButton = rootView.findViewById<ImageView>(R.id.button_back)
        goBackButton.setOnClickListener(this)

        //аватарка
        val avatar = rootView.findViewById<ImageView>(R.id.avatar)
        avatar.setImageResource(R.drawable.baseline_account_circle_24)

        //кому отправляем сообщение
        val sendToName = rootView.findViewById<TextView>(R.id.user_name_text_view)
        sendToName.text = getSendTo()

        setMessages()
    }

    private fun setMessages(){
        //показываем сообщения
        messagesList = getMessages() as ArrayList<JSONObject>
        messagesList.reverse()
        messageList = rootView.findViewById(R.id.messages_list)
        messageList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            true
        )
        messagesListAdapter = ChatListAdapter(messagesList, requireActivity())
        messageList.adapter = messagesListAdapter
    }

    private fun getSendTo(): String {
        //кому отправляем сообщение
        val args = requireArguments()
        return args.getString("sendTO").toString()
    }

    private fun getMessages() : List<JSONObject> {
        //получение сообщений, сохранённых локально
        val l : MutableList<JSONObject> = mutableListOf()
        val msgs = database.messageDao().getAllByUniqueID(uniqueRoomID)
        for (msg in msgs){
            val obj = Gson().toJson(msg)
            l.add(JSONObject(obj))
        }
        return l
    }

    private fun setVars() {
        //получаение статичных переменных
        preferences = requireActivity().getSharedPreferences(
            StaticVars().preferencesName,
            Context.MODE_PRIVATE
        )
        myNickname = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_NICKNAME, "").toString()
        database = AppDatabase.getAppDataBase(requireContext())!!
        roomDao = database.chatRoomDao()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.send_message_button -> {
                //отправка сообщения
                val messageField = requireView().findViewById<EditText>(R.id.message_text_field)
                if (!messageField.text.isNullOrEmpty()) {
                    val message = MessageModel(
                        messageField.text.toString(),
                        uniqueRoomID,
                        myNickname
                    )
                    mSocket.emit(socketEvents.EVENT_CHAT_MESSAGE, message.toJson())

                    //сохраняем полученное сообщение
                    val msgLocal = MessageLocalModel(null, messageField.text.toString(), uniqueRoomID, myNickname)
                    database.messageDao().insert(msgLocal)

                    messageField.text.clear()
                    messageField.clearFocus()
                }
            }

            R.id.button_back -> {
                requireActivity().findNavController(R.id.nav_host_fragment).navigateUp()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        //событие получения сообщения
        requireActivity().runOnUiThread {
            val data = JSONObject(args[0].toString())
            //addMessage(data)
        }
    }

    /*
    private fun addMessage(data: JSONObject) {
        //messagesList.add(data)
        //messagesListAdapter.notifyDataSetChanged()
        //сохраняем в сокет-сервисе
        //setMessages()
    }

     */

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).visibility = View.VISIBLE
    }
}