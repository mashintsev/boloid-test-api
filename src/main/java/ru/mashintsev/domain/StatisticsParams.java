package ru.mashintsev.domain;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by mashintsev@gmail.com on 18.08.15.
 */
public class StatisticsParams {

    private String status;
    private Date timestamp;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "StatisticsParams{" +
                "status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
