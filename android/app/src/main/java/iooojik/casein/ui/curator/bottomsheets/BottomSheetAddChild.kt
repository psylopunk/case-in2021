package iooojik.casein.ui.curator.bottomsheets

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import iooojik.casein.LogMessages
import iooojik.casein.MainActivity
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.chatRooms.ChatRoomModel
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.ui.curator.CuratorHomeFragment
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.request.AddChildRequest
import iooojik.casein.web.models.response.Child
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BottomSheetAddChild  (
    private val context: Context,
    private val activity: Activity,
    private val fragment: CuratorHomeFragment
) : View.OnClickListener{

    private val bottomView : View = activity.layoutInflater.inflate(R.layout.bottom_sheet_add_child, null)
    val bottomSheetDialog : BottomSheetDialog = BottomSheetDialog(context)
    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
    private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!
    private val netModel = NetModel()
    private val token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()
    private val mainActivity = MainActivity()


    init {
        //"подготовка" bottomSheetDialog
        initialize()
        bottomSheetDialog.setContentView(bottomView)
    }

    private fun initialize() {
        //слушатель на кнопку
        bottomView.findViewById<Button>(R.id.button_add_child).setOnClickListener(this)
    }

    private fun addChild(fio: String, login: String, password: String) {
        //запрос на добавление нового сотрудника

        val reqModel = AddChildRequest()
        reqModel.fullName = fio
        reqModel.login = login
        reqModel.password = password
        netModel.childApi.addChild(reqModel, token).enqueue(object : Callback<Child>{
            override fun onResponse(call: Call<Child>, response: Response<Child>) {
                if (response.isSuccessful){
                    try {

                        val resp = response.body()!!
                        val childModel = ChildModel(null, resp.fullName, resp.login,
                                resp.parent.fullName, resp.parent.id, resp.created, resp.id)

                        database.chatRoomDao().insert(ChatRoomModel(null, resp.fullName, resp.id))

                        database.childModelDao().insert(childModel)

                        fragment.checkData()
                        mainActivity.showSnackbar("Добавлено", fragment.rootView)

                    } catch (e : Exception){

                        mainActivity.showSnackbar("Не удалось добавить пользователя", fragment.rootView)
                        Log.e("childApi error", e.toString())

                    }

                    bottomSheetDialog.hide()

                }
            }

            override fun onFailure(call: Call<Child>, t: Throwable) {
                mainActivity.showSnackbar("Не удалось добавить пользователя", fragment.rootView)
                Log.e("childApi error", t.toString())
            }
        })
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_add_child -> {

                //кнопка добавления нового сотрудника
                val fio = bottomView.findViewById<EditText>(R.id.full_name_field).text.toString()
                val login = bottomView.findViewById<EditText>(R.id.login_field).text.toString()
                val password = bottomView.findViewById<EditText>(R.id.password_field).text.toString()

                addChild(fio, login, password)

            }

        }
    }

}