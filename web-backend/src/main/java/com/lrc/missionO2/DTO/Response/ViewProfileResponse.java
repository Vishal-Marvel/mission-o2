package com.lrc.missionO2.DTO.Response;

import com.lrc.missionO2.entity.Address;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ViewProfileResponse {
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String gender;
    private Address address;
    private Date dob;
//    private String proof;
    private Long totalPlants;
    private Long totalOrders;

}
