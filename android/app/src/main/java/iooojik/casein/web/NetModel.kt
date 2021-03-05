package iooojik.casein.web

import iooojik.casein.StaticVars
import iooojik.casein.web.api.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetModel {

    private lateinit var retrofit: Retrofit
    //api
    lateinit var authorizationApi : AuthorizationApi
    lateinit var childApi: ChildApi
    lateinit var tasksApi: TasksApi
    lateinit var botApi: BotApi
    lateinit var scoreboardApi: ScoreboardApi

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
        scoreboardApi = retrofit.create(ScoreboardApi::class.java)
    }
}