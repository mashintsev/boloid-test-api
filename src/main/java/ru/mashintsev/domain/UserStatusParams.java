package ru.mashintsev.domain;

/**
 * Created by mashintsev@gmail.com on 18.08.15.
 */
public class UserStatusParams {
    private String userId;
    private String status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserStatusParams{" +
                "userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
