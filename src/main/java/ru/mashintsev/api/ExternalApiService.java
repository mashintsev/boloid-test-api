package ru.mashintsev.api;

import org.springframework.stereotype.Service;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
@Service
public class ExternalApiService {

    public void updateUserStatus(String userId, String status) {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
