package com.lrc.missionO2.DTO.Request;

import com.lrc.missionO2.entity.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetProfileRequest {
    @NotNull(message = "Name is mandatory")
    private String name;
    @Email(message = "Enter Valid Email")
    private String email;
    private Address address;
    private Date dob;
//    private MultipartFile file;

}
