package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.AppLoginRequest;
import com.lrc.missionO2.DTO.Request.AuthenticationRequest;
import com.lrc.missionO2.DTO.Request.RegisterRequest;
import com.lrc.missionO2.DTO.Request.SetProfileRequest;
import com.lrc.missionO2.DTO.Response.AuthenticationResponse;
import com.lrc.missionO2.DTO.Response.ViewProfileResponse;
import com.lrc.missionO2.entity.Address;
import com.lrc.missionO2.entity.FileData;
import com.lrc.missionO2.entity.User;
import com.lrc.missionO2.entity.UserRole;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.exceptions.UserNotFoundException;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.UserRepo;
import com.lrc.missionO2.security.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final FileRepo fileRepo;
    private final JWTTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private static final String CHARACTERS = "0123456789";
    private static final int ID_LENGTH = 6;

    private byte[] getProof(String id){
        return fileRepo.findById(id).orElseThrow(()->new ItemNotFoundException("User Image with id: " + id + "not Found"))
                .getData();
    }

    public static String generateUniqueID() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(ID_LENGTH);

        for (int i = 0; i < ID_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public String registerViaApp(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepo.findByMobile(registerRequest.getMobile());

        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setMobile(registerRequest.getMobile());
            user.setPassword(passwordEncoder.encode(registerRequest.getMobile()));
            user.setRole(UserRole.ROLE_USER);
            user.setVerified(false);
            user.setOTP(generateUniqueID());
            user.setOTPLimit(new Date(new Date().getTime() + 1000*60*60));
            //TODO: OTP services

            userRepo.save(user);
            return "User Registered";
        }
        else{
            User user = optionalUser.get();
            user.setOTP(generateUniqueID());
            user.setOTPLimit(new Date(new Date().getTime() + 1000*60*60));
            userRepo.save(user);
            return "OTP Sent";
        }
    }


    public AuthenticationResponse authenticatePortal(AuthenticationRequest authenticationRequest) {


        User user = userRepo.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User Not found"));
        if ( !passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())){
            throw new AccessDeniedException("Bad Credentials");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword());
        userRepo.save(user);
        return AuthenticationResponse.builder()
                .token(jwtTokenProvider
                        .generateToken(auth, false))
                .role(user.getRole().name())
                .build();
    }

    public AuthenticationResponse authenticateApp(AppLoginRequest appLoginRequest) {

        User user = userRepo.findByMobile(appLoginRequest.getMobile())
                .orElseThrow(() -> new UsernameNotFoundException("User Not found"));

        if (!Objects.equals(user.getOTP(), appLoginRequest.getOtp()) || new Date().after(user.getOTPLimit())){

            throw new AccessDeniedException("Invalid OTP");
        }

        user.setOTPLimit(null);
        user.setOTP(null);
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword());

        userRepo.save(user);
        return AuthenticationResponse.builder()
                .token(jwtTokenProvider
                        .generateToken(auth, true))
                .role(user.getRole().name())
                .build();
    }

    public String setProfile(SetProfileRequest setProfileRequest) throws IOException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findById(id)
                .orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setEmail(setProfileRequest.getEmail());
        user.setUsername(setProfileRequest.getName());
        user.setDob(setProfileRequest.getDob());
        user.setAddress(Address.builder()
                .addressLine1(setProfileRequest.getAddress())
                .city(setProfileRequest.getCity())
                .state(setProfileRequest.getState())
                .build());
        FileData file = new FileData();
        file.setData(setProfileRequest.getFile().getBytes());
        fileRepo.save(file);
        user.setProof(file.getId());
        userRepo.save(user);
        return "User Profile Set";
    }


    public ViewProfileResponse viewProfile(String userId) {
        User user = null;
        if (Objects.isNull(userId)){
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            user = userRepo.findById(id)
                    .orElseThrow(()->new UserNotFoundException("User Not Found"));
        }
        else{
            user = userRepo.findById(userId)
                    .orElseThrow(()->new UserNotFoundException("User Not Found"));
        }
        return ViewProfileResponse.builder()
                .address(user.getAddress().getAddressLine1())
                .email(user.getEmail())
                .proof(getProof(user.getProof()))
                .mobile(user.getMobile())
                .city(user.getAddress().getCity())
                .state(user.getAddress().getState())
                .build();

     }

    public String verifyProfile(String userId) {
        User user = userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setVerified(true);
        userRepo.save(user);
        return "User Verified";
    }
}
