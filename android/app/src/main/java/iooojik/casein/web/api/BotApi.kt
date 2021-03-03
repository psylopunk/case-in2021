package iooojik.casein.web.api

import iooojik.casein.web.models.request.SendMessageModel
import iooojik.casein.web.models.response.MessagesResponse
import retrofit2.Call
import retrofit2.http.*

interface BotApi {

    //запрос на создание нового сотрудника
    @POST("/api/chatbot.sendMessage")
    fun sendMessage(@Body sendMessageModel: SendMessageModel, @Header("Authorization") token : String) : Call<Any>

    @GET("/api/chatbot.getMessages?")
    fun getMessages(@Query("offset") offset : String, @Query("limit") limit : String,
                  @Header("Authorization") token : String) : Call<List<MessagesResponse>>
}