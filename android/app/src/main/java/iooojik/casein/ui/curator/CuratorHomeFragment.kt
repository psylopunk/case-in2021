package iooojik.casein.ui.curator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.chatRooms.ChatRoomModel
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.ui.curator.bottomsheets.BottomSheetAddChild
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.response.Child
import iooojik.casein.web.models.response.Childs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.concurrent.thread

class CuratorHomeFragment : Fragment(), View.OnClickListener {

    lateinit var rootView : View
    private lateinit var database: AppDatabase
    private lateinit var preferences: SharedPreferences
    private lateinit var childsList : ArrayList<ChildModel>
    private val netModel = NetModel()
    private val fragment = this
    lateinit var childsAdapter : ChildsAdapter
    lateinit var recChilds : RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_curator_home, container, false)
        initialization()
        return rootView
    }

    private fun initialization() {
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        database = AppDatabase.getAppDataBase(requireContext())!!

        requireActivity().findViewById<FloatingActionButton>(R.id.fab).show()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).setOnClickListener(this)

        showChilds()
        initViews()
    }

    private fun showChilds(){
        //отображение списка "детей"
        recChilds = rootView.findViewById(R.id.rec_childs_list)
        recChilds.layoutManager = LinearLayoutManager(requireContext())

        childsList = database.childModelDao().getAll() as ArrayList<ChildModel>
        childsAdapter = ChildsAdapter(requireContext(), requireActivity(), childsList, fragment)
        recChilds.adapter = childsAdapter

        thread {
            getChilds()
        }
    }

    private fun initViews(){
        //отображение прочей информации

        val userNameField = rootView.findViewById<TextView>(R.id.user_name_text_view)
        userNameField.text = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_NAME, "").toString()

    }

    fun checkData(){
        requireActivity().runOnUiThread {
            if (database.childModelDao().getAll().isNotEmpty()){
                val lastModel = database.childModelDao().getAll().last()
                if (!searchMatches(lastModel))
                    childsList.add(lastModel)
            } else childsList.clear()
            recChilds.adapter?.notifyDataSetChanged()
        }
    }

    private fun searchMatches(child : ChildModel) : Boolean{
        var matches = false

        childsList.forEachIndexed { _, childModel ->
            if (childModel.login.equals(child.login)) matches = true
        }

        return matches
    }

    private fun getChilds() {

        val token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()

        netModel.childApi.getChilds(token).enqueue(object : Callback<Childs>{
            override fun onResponse(call: Call<Childs>, response: Response<Childs>) {
                if (response.isSuccessful){

                    val childs = response.body()?.childs

                    //проверка на изменения
                    var matches = false

                    childs?.forEachIndexed { _, child ->

                        childsList.forEachIndexed { index, childModel ->

                            //проверяем совпадения по логину
                            //если есть совпадения, то идём дальше, иначе добавляем модельки в бд
                            //проверять на соотвествие данным бессмысленно, так как ничего не редактируется

                            if (childModel.login.equals(child.login)){
                                if (!matches) matches = true
                            }

                            if (!matches){
                                database.childModelDao().insert(ChildModel(null, child.fullName, child.login,
                                        child.parent.fullName, child.parent.id, child.created, child.id))

                                database.chatRoomDao().insert(ChatRoomModel(null, child.fullName, child.id))

                            }
                        }
                        /*
                        if (isChildModelInInLocalData(child)){
                            database.childModelDao().insert(ChildModel(null, child.fullName, child.login,
                                    child.parent.fullName, child.parent.id, child.created, child.id))
                            anyChanges = true
                        }

                        if (isRoomModelInInLocalData(child)){
                            database.chatRoomDao().insert(ChatRoomModel(null, child.fullName, child.id))
                            anyChanges = true
                        }

                         */
                    }


                    //если есть какие-то изменения, то обновляем recyclerView
                    if (matches)
                        try {
                            requireActivity().runOnUiThread {
                                checkData()
                            }
                        } catch (e : Exception){
                            e.printStackTrace()
                        }



                } else Log.e("getChilds error", response.raw().toString())
            }

            override fun onFailure(call: Call<Childs>, t: Throwable) {
                Log.e("getChilds error", t.toString())
            }
        })
    }

    private fun isChildModelInInLocalData(childModel : Child) : Boolean{
        val status: Boolean
        val foundModel : ChildModel = database.childModelDao().getByModelID(childModel.id)
        status = foundModel == null
        return status
    }

    private fun isRoomModelInInLocalData(roomModel : Child) : Boolean{
        val status: Boolean
        val foundModel : ChatRoomModel = database.chatRoomDao().getByUniqueID(roomModel.id)
        status = foundModel == null
        return status
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fab -> {
                val bottomSheetAddChild = BottomSheetAddChild(requireContext(), requireActivity(), fragment).bottomSheetDialog
                bottomSheetAddChild.show()
            }
        }
    }

}