package iooojik.casein.ui.beginner.rating

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase
import iooojik.casein.web.NetModel
import iooojik.casein.web.models.response.Scoreboard
import iooojik.casein.web.models.response.ScoreboardResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class RatingFragment : Fragment() {

    private lateinit var rootView : View
    private lateinit var preferences: SharedPreferences
    private lateinit var token : String
    private lateinit var database: AppDatabase
    private val netModel = NetModel()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView =  inflater.inflate(R.layout.fragment_rating, container, false)
        initViews()
        return rootView
    }

    private fun initViews(){
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        token  = preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_TOKEN, "").toString()
        database = AppDatabase.getAppDataBase(requireContext())!!
        thread {
            showRating()
        }
    }

    private fun showRating() {
        netModel.scoreboardApi.getScoreboard(token).enqueue(object : Callback<ScoreboardResponse>{
            override fun onResponse(
                call: Call<ScoreboardResponse>,
                response: Response<ScoreboardResponse>
            ) {
                if (response.isSuccessful){

                    val resp = response.body()
                    if (resp != null){
                        val currPos = resp.place
                        val scores = resp.scoreboard
                        showRatingInView(currPos, scores)
                    }


                } else Log.e("error", response.raw().toString())
            }

            override fun onFailure(call: Call<ScoreboardResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun showRatingInView(currPos: Int, scores: MutableList<Scoreboard>) {
        val recView = rootView.findViewById<RecyclerView>(R.id.rec_rating)
        val adapter = RatingAdapter(requireContext(), requireActivity(), scores, currPos, preferences.getString(StaticVars().PREFERENCES_CURRENT_USER_NICKNAME, "").toString())
        recView.layoutManager = LinearLayoutManager(requireContext())
        recView.adapter = adapter
    }
}