package com.lrc.missionO2.entity;

import com.lrc.missionO2.validators.ValidAddress;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidAddress(message = "All Address Fields are mandatory")
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String pinCode;
    private String state;
    private String district;
    private String taluk;
    private String country;

}
