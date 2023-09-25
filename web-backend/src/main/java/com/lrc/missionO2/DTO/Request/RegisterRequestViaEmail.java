package com.lrc.missionO2.DTO.Request;

import com.lrc.missionO2.entity.Address;
import com.lrc.missionO2.validators.ValidAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequestViaEmail {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Enter Valid Email")
    private String email;

    @NotBlank(message = "Mobile Number is mandatory")
    @Size(min = 10, max = 10, message = "Mobile number should have 10 digits")
    private String mobile;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must by minimum of 8 characters")
    private String password;

    @NotNull(message = "Address must not be null")
    @Valid
    private Address address;

    @NotNull(message = "DOB must not be null")
    private Date dob;

    @NotBlank(message = "Gender is mandatory")
    private String gender;
}
