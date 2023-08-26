package com.lrc.missionO2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * An exception class representing a user not found exception.
 * This class extends the RuntimeException class and is used to handle exceptions when a user is not found.
 * It holds information about the exception message.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
