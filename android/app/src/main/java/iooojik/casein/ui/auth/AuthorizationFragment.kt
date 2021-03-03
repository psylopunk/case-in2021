package iooojik.casein.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.casein.R
import iooojik.casein.localData.AppDatabase
import iooojik.casein.web.NetModel
import iooojik.casein.StaticVars
import iooojik.casein.localData.chatRooms.ChatRoomModel
import iooojik.casein.localData.childs.ChildModel
import iooojik.casein.web.models.request.LoginRequest
import iooojik.casein.web.models.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthorizationFragment : Fragment(), View.OnClickListener {

    private val netModel = NetModel()
    private lateinit var database: AppDatabase
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    private fun initialize() {
        database = AppDatabase.getAppDataBase(requireContext())!!
        database.clearAllTables()
        preferences = requireActivity().getSharedPreferences(
            StaticVars().preferencesName,
            Context.MODE_PRIVATE
        )
        preferences.edit().clear().apply()
        //кнопка авторизации и слушатель на неё
        val signInButton = requireView().findViewById<Button>(R.id.sign_in_button)
        signInButton.setOnClickListener(this)
        //убираем кнопку fab
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).hide()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.sign_in_button -> {
                //получаем никнейм пользователя
                val login = requireView().findViewById<EditText>(R.id.nickname_field).text.toString()
                //получаем пароль пользователя
                val password = requireView().findViewById<EditText>(R.id.password_field).text.toString()
                val loginRequest = LoginRequest()
                loginRequest.login = login
                loginRequest.password = password
                //запрос на авторизацию
                netModel.authorizationApi.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful){
                            val model = response.body()
                            if (model?.profile != null) {
                                saveInformation(model, password)
                            }
                        }else Log.e("auth error", response.toString())
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }
        }
    }

    private fun saveInformation(model: LoginResponse, password: String) {
        saveToSharedPreferences(model, password)
    }

    private fun getHomeFragment() : Int{
        return preferences.getInt(StaticVars().PREFERENCES_CURRENT_USER_START_FRAGMENT, R.id.navigation_beginner_home)
    }

    private fun saveToSharedPreferences(model: LoginResponse, password: String) {
        //записываем данные в SharedPreferences

        //сохранение логина
        preferences.edit().putString(
                StaticVars().PREFERENCES_CURRENT_USER_NICKNAME,
                model.profile.login
        ).apply()

        //сохранение токена
        preferences.edit().putString(
                StaticVars().PREFERENCES_CURRENT_USER_TOKEN,
                model.token
        ).apply()

        //сохранение фио
        preferences.edit().putString(
            StaticVars().PREFERENCES_CURRENT_USER_NAME,
            model.profile.fullName
        ).apply()

        //сохранение id "создателя"
        preferences.edit().putString(
            StaticVars().PREFERENCES_PARENT_ID,
            model.profile.parent?.id.toString()
        ).apply()

        //сохранение фио "создателя"
        preferences.edit().putString(
            StaticVars().PREFERENCES_PARENT_NAME,
            model.profile.parent?.fullName.toString()
        ).apply()

        //если всего создалей двое(админ и куратор), то это аккаунт новичка, иначе - куратора
        if (model.depth == 2){
            //меню
            preferences.edit().putInt(StaticVars().PREFERENCES_CURRENT_USER_NAV_MENU, R.menu.begginer_nav_menu).apply()
            //начальный фрагмент
            preferences.edit().putInt(StaticVars().PREFERENCES_CURRENT_USER_START_FRAGMENT, R.id.navigation_beginner_home).apply()
            //id профиля
            preferences.edit().putString(StaticVars().PREFERENCES_USER_ID, model.profile.id).apply()
            val m = ChildModel(null, model.profile.parent?.fullName.toString(),  model.profile.login,
                    model.profile.fullName, model.profile.id, model.profile.created, model.profile.id)
            database.childModelDao().insert(m)
        } else {
            //меню
            preferences.edit().putInt(StaticVars().PREFERENCES_CURRENT_USER_NAV_MENU, R.menu.curator_nav_menu).apply()
            //начальный фрагмент
            preferences.edit().putInt(StaticVars().PREFERENCES_CURRENT_USER_START_FRAGMENT, R.id.navigation_curator_home).apply()
            saveChilds(model)
        }

        //изменяем меню в зависимости от типа аккаунта
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).menu.clear()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).
        inflateMenu(
            preferences.getInt(
                StaticVars().PREFERENCES_CURRENT_USER_NAV_MENU, R.menu.begginer_nav_menu
            )
        )
        preferences.edit().putInt(StaticVars().PREFERENCES_CURRENT_USER_ACCOUNT_TYPE, model.depth).apply()

        //переходим на следующий фрагмент
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(getHomeFragment())
    }

    private fun saveChilds(model: LoginResponse) {
        //добавляем childs в локльную бд, если они есть
        val childs = model.childs
        childs.forEach {
            database.childModelDao().insert(ChildModel(null, it.fullName, it.login, it.parent.fullName, it.parent.id, it.created, it.id))
            database.chatRoomDao().insert(ChatRoomModel(null, it.fullName, it.id))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).visibility = View.VISIBLE
    }
}