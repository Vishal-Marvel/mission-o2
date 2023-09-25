package com.lrc.missionO2.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
/**
 * An exception class representing an API exception.
 * This class extends the RuntimeException class and is used to handle exceptions that occur during API operations.
 * It holds information about the exception message and the corresponding HTTP status.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class APIException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public APIException(String message,  HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;

    }
}
