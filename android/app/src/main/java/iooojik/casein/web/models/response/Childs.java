package iooojik.casein.web.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Childs {
    @SerializedName("childs")
    @Expose
    private List<Child> childs = null;
    @SerializedName("depth")
    @Expose
    private Integer depth;

    public List<Child> getChilds() {
        return childs;
    }

    public void setChilds(List<Child> childs) {
        this.childs = childs;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
