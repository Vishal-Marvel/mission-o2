package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.AuthenticationRequest;
import com.lrc.missionO2.DTO.Request.RegisterRequest;
import com.lrc.missionO2.DTO.Response.AuthenticationResponse;
import com.lrc.missionO2.entity.User;
import com.lrc.missionO2.entity.UserRole;
import com.lrc.missionO2.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;


    public void register(RegisterRequest registerRequest) {
        //OTP services
        Optional<User> optionalUser = userRepo.findByMobile(registerRequest.getMobile());
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setMobile(registerRequest.getMobile());
            user.setRole(UserRole.ROLE_USER);
            user.setVerified(false);
//            sendMail(user, "verify", "Registration");
            userRepo.save(user);

        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        return new AuthenticationResponse();
    }
}
