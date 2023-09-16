package com.lrc.missionO2.DTO.Response;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponse<T> {
    private List<T> data;
    private Integer totalPages;
    private Long total;
}
