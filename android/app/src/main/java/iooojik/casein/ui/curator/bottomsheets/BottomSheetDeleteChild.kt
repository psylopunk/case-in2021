package iooojik.casein.ui.curator.bottomsheets

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.ui.curator.CuratorHomeFragment
import iooojik.casein.web.NetModel
import iooojik.casein.MainActivity
import iooojik.casein.web.models.request.RemoveRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BottomSheetDeleteChild(
    context: Context,
    private val activity: Activity,
    private val model: ChildModel,
    private val fragment: CuratorHomeFragment
) : View.OnClickListener{

    private val bottomView : View = activity.layoutInflater.inflate(R.layout.bottom_sheet_delete, null)
    val bottomSheetDialog : BottomSheetDialog = BottomSheetDialog(context)
    private val preferences = activity.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
    private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!
    private val netModel = NetModel()
    private val token = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()


    init {
        //"подготовка" bottomSheetDialog

        initialize()
        bottomSheetDialog.setContentView(bottomView)
    }

    private fun initialize(){
        bottomView.findViewById<Button>(R.id.delete_button).setOnClickListener(this)
        bottomView.findViewById<Button>(R.id.cancel_button).setOnClickListener(this)
    }

    private fun delete(){
        //запрос на удаление

        val r = RemoveRequest()
        r.userId = model.modelId

        netModel.childApi.deleteChild(r, token).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

                if (response.isSuccessful){

                    database.childModelDao().delete(model)
                    database.chatRoomDao().deleteByUniqueID(model.modelId.toString())

                    fragment.checkData()
                    bottomSheetDialog.hide()

                    MainActivity().showSnackbar("Удалено", fragment.rootView)



                } else Log.e("childApi error", response.raw().toString())
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                t.printStackTrace()
                try {
                    delete()
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.cancel_button -> {
                bottomSheetDialog.hide()
            }
            R.id.delete_button -> {
                delete()
            }
        }

    }


}