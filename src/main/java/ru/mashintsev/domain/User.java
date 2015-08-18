package ru.mashintsev.domain;

import java.util.Date;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
public class User {

    private String id;
    private String photoUri;
    private String login;
    private String email;
    private String status;
    private Date statusTimestamp;

    public User() {
    }

    public User(String id, String photoUri, String login, String email, String status, Date status_timestamp) {
        this.id = id;
        this.photoUri = photoUri;
        this.login = login;
        this.email = email;
        this.status = status;
        this.statusTimestamp = status_timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setStatusTimestamp(Date statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", statusTimestamp=" + statusTimestamp +
                '}';
    }
}
