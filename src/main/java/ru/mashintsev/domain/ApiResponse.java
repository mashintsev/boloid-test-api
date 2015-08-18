package ru.mashintsev.domain;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
public class ApiResponse {

    private Boolean success;
    private String error;
    private Object result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
