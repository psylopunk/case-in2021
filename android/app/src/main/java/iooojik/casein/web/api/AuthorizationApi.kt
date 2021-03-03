package iooojik.casein.web.api

import iooojik.casein.web.models.request.LoginRequest
import iooojik.casein.web.models.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthorizationApi {
    //запрос на авторизацию
    @POST("/api/auth.signIn")
    fun login(@Body loginRequest: LoginRequest) : Call<LoginResponse>

}