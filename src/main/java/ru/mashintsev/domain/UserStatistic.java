package ru.mashintsev.domain;

import java.util.Date;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
public class UserStatistic {

    private String status;
    private String photoUri;
    private Date timestamp;

    public UserStatistic() {
    }

    public UserStatistic(String photoUri, String status, Date timestamp) {
        this.photoUri = photoUri;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
