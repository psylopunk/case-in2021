package iooojik.casein.web

import iooojik.casein.StaticVars
import iooojik.casein.web.api.AuthorizationApi
import iooojik.casein.web.api.BotApi
import iooojik.casein.web.api.ChildApi
import iooojik.casein.web.api.TasksApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetModel {

    private lateinit var retrofit: Retrofit
    //api
    lateinit var authorizationApi : AuthorizationApi
    lateinit var childApi: ChildApi
    lateinit var tasksApi: TasksApi
    lateinit var botApi: BotApi

    init {
        doRetrofit()
    }

    private fun doRetrofit(){
        retrofit = Retrofit.Builder()
            .baseUrl(StaticVars().API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        authorizationApi = retrofit.create(AuthorizationApi::class.java)
        childApi = retrofit.create(ChildApi::class.java)
        tasksApi = retrofit.create(TasksApi::class.java)
        botApi = retrofit.create(BotApi::class.java)
    }
}