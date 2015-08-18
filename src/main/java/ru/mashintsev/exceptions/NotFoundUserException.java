package ru.mashintsev.exceptions;

/**
 * Created by mashintsev@gmail.com on 18.08.15.
 */
public class NotFoundUserException extends Exception {
    public NotFoundUserException() {
    }

    public NotFoundUserException(String s) {
        super(s);
    }

    public NotFoundUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
