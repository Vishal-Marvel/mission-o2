package com.lrc.missionO2.DTO.Request;

import com.lrc.missionO2.entity.Address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
public class SetProfileRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Enter Valid Email")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Mobile Number is mandatory")
    @Size(min = 10, max = 10, message = "Mobile number should have 10 digits")
    private String mobile;

    @NotBlank(message = "Gender is mandatory")
    private String gender;

    @NotNull(message = "Address is mandatory")
    @Valid
    private Address address;

    @NotNull(message = "Dob is mandatory")
    private Date dob;

}
