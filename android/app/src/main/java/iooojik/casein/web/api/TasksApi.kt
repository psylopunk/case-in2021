package iooojik.casein.web.api

import iooojik.casein.web.models.request.AddTaskRequest
import iooojik.casein.web.models.request.EditTaskRequest
import iooojik.casein.web.models.response.tasks.TasksResponse
import retrofit2.Call
import retrofit2.http.*

interface TasksApi {

    @GET("/api/tasks.get")
    fun getTasks(@Header("Authorization") token : String) : Call<List<TasksResponse>>

    @POST("/api/tasks.create")
    fun addTask(@Body addTaskRequest: AddTaskRequest,  @Header("Authorization") token : String) : Call<TasksResponse>

    @POST("/api/tasks.edit")
    fun editTask(@Body editTaskRequest: EditTaskRequest,  @Header("Authorization") token : String) : Call<TasksResponse>

    @GET("/api/tasks.get?")
    fun getCompletedTasks(@Query("status") status : String, @Query("userId") userId : String,
                          @Header("Authorization") token : String) : Call<List<TasksResponse>>
    
}