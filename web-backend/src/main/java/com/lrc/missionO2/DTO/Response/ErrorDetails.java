package com.lrc.missionO2.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * This class represents the error details that are returned by the REST API in case of an exception.
 * It contains the following fields:
 * - timestamp: the date and time when the error occurred
 * - message: the summary of the error
 * - details: the additional information about the error
 */
@Data
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private Object message;
    private String details;
}
