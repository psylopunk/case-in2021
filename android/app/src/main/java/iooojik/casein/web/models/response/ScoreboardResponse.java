package iooojik.casein.web.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScoreboardResponse  {
    @SerializedName("place")
    @Expose
    private Integer place;
    @SerializedName("scoreboard")
    @Expose
    private List<Scoreboard> scoreboard = null;

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public List<Scoreboard> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(List<Scoreboard> scoreboard) {
        this.scoreboard = scoreboard;
    }
}
