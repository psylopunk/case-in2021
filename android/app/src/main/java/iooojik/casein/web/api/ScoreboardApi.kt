package iooojik.casein.web.api

import iooojik.casein.web.models.response.Childs
import iooojik.casein.web.models.response.ScoreboardResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ScoreboardApi {

    @GET("/api/scoreboard.get")
    fun getScoreboard(@Header("Authorization") token : String) : Call<ScoreboardResponse>

}