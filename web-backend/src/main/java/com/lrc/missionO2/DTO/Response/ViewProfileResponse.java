package com.lrc.missionO2.DTO.Response;

import com.lrc.missionO2.entity.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewProfileResponse {
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String address;
    private String city, state;
    private byte[] proof;

}
