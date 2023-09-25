package com.lrc.missionO2.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidArgumentException extends RuntimeException{
    private final List<FieldError> errors;
    public InvalidArgumentException(List<FieldError> errors){
        this.errors = errors;
    }

}
