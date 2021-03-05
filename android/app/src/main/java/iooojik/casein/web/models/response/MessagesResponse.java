package iooojik.casein.web.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessagesResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("keyboard")
    @Expose
    private List<String> keyboard = null;
    @SerializedName("incoming")
    @Expose
    private Boolean incoming;
    @SerializedName("created")
    @Expose
    private String created;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(List<String> keyboard) {
        this.keyboard = keyboard;
    }

    public Boolean getIncoming() {
        return incoming;
    }

    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
