package iooojik.casein

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import iooojik.casein.background.SocketService
import iooojik.casein.background.process.WebSocketsService

@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS", "DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var preferences : SharedPreferences
    private lateinit var navController : NavController
    private lateinit var socketService: SocketService
    private val logMessages = LogMessages()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialization()
    }

    private fun initialization(){
        socketService = SocketService(this, Intent(this, WebSocketsService::class.java))

        //получаем SharedPreferences
        preferences = this.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)

        //настраиваем навигацию
        navigationSetup()

        //запускаем сервис подключения к сокетам
        socketService.startSocketService()

        //проверяем, авторизован ли пользователь
        if (isNotAuth()) findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_authorization)
        else findNavController(R.id.nav_host_fragment).navigate(getHomeFragment())
    }


    private fun isNotAuth() : Boolean{
        Log.i(logMessages.AUTH_MESSAGE, "USER AUTH: " +  preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "")
                .isNullOrEmpty())
        return preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").isNullOrEmpty()
    }

    private fun navigationSetup(){

        //контроллер навигации
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        //views
        val drawer : DrawerLayout = findViewById(R.id.drawer)
        AppBarConfiguration.Builder(getHomeFragment()).setDrawerLayout(drawer).build()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.inflateMenu(getMenu())
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        Log.i(logMessages.NAVIGATION_MESSAGE, "NAVIGATION WAS SET UP")

    }

    private fun getHomeFragment() : Int{
        return if (preferences.getInt(StaticVars().PREFERENCES_CURRENT_USER_ACCOUNT_TYPE, 0) == 2)
            R.id.navigation_beginner_home
        else R.id.navigation_curator_home
    }

    private fun getMenu() : Int {
        return if (preferences.getInt(StaticVars().PREFERENCES_CURRENT_USER_ACCOUNT_TYPE, 0) == 2)
            R.menu.begginer_nav_menu
        else R.menu.curator_nav_menu
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        //убираем клавиатуру, если нет фокуса на edit text
        if (ev?.action === MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                v.clearFocus()
                val imm: InputMethodManager =
                    applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun showSnackbar(message : String, view : View){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

}