package com.lrc.missionO2.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * An exception class representing a constraint exception.
 * This class extends the RuntimeException class and is used to handle constraint-related exceptions.
 * It holds information about the exception message.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConstraintException extends RuntimeException{
    public ConstraintException(String message) {
        super(message);
    }
}
