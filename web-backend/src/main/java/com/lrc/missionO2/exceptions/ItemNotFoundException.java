package com.lrc.missionO2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * An exception class representing an item not found exception.
 * This class extends the RuntimeException class and is used to handle exceptions when an item is not found.
 * It holds information about the exception message.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException{
    public ItemNotFoundException(String message) {
        super(message);
    }
}
