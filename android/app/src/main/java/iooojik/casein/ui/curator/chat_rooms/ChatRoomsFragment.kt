package iooojik.casein.ui.curator.chat_rooms

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.casein.R
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.chatRooms.ChatRoomDao
import iooojik.casein.localData.chatRooms.ChatRoomModel


class ChatRoomsFragment : Fragment(), View.OnClickListener {

    private lateinit var rootView : View
    private lateinit var database: AppDatabase
    private lateinit var roomDao: ChatRoomDao
    private lateinit var roomsAdapter : ChatRoomsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_chat_rooms, container, false)
        initViews()
        return rootView
    }

    private fun initViews() {

        makeVars()
        loadInformationFromDB()

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.hide()

    }

    private fun makeVars(){
        //получение статических переменных
        database = AppDatabase.getAppDataBase(requireContext())!!
        roomDao = database.chatRoomDao()
    }

    override fun onClick(v: View?) {
        when(v!!.id){

        }
    }

    private fun loadInformationFromDB(){
        //получение информации из локальной бд
        val rooms = roomDao.getAll()
        showInformation(rooms)
    }

    private fun showInformation(rooms: List<ChatRoomModel>) {
        //отображение информации во фрагменте
        val roomsRecView = rootView.findViewById<RecyclerView>(R.id.rec_view_rooms_list)
        roomsRecView.layoutManager = LinearLayoutManager(context)
        roomsAdapter = ChatRoomsAdapter(requireContext(), requireActivity(), rooms)
        roomsRecView.adapter = roomsAdapter
    }

}