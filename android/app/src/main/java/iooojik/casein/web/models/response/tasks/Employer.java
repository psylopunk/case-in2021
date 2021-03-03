package iooojik.casein.web.models.response.tasks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import iooojik.casein.web.models.response.Parent;

public class Employer {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("parent")
    @Expose
    private Parent parent;
    @SerializedName("created")
    @Expose
    private String created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
