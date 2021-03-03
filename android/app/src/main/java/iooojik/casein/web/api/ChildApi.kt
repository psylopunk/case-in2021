package iooojik.casein.web.api

import iooojik.casein.web.models.request.AddChildRequest
import iooojik.casein.web.models.request.RemoveRequest
import iooojik.casein.web.models.response.Child
import iooojik.casein.web.models.response.Childs
import retrofit2.Call
import retrofit2.http.*

interface ChildApi {

    //запрос на создание нового сотрудника
    @POST("/api/account.addEmployee")
    fun addChild(@Body addChildRequest: AddChildRequest, @Header("Authorization") token : String) : Call<Child>

    @GET("/api/account.getChilds")
    fun getChilds(@Header("Authorization") token : String) : Call<Childs>

    @POST("/api/account.removeEmployee")
    fun deleteChild(@Body removeRequest: RemoveRequest, @Header("Authorization") token : String) : Call<Any>
}