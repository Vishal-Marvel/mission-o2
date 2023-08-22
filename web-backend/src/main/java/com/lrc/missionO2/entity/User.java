package com.lrc.missionO2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id = UUID.randomUUID().toString();
    private String username;
    private String password;
    private String mobile;
    private Address address;
    private String email;
    private UserRole role;
    private String OTP;
    private Date OTPLimit;
    private boolean verified;

}
