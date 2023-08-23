package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.AuthenticationRequest;
import com.lrc.missionO2.DTO.Request.RegisterRequest;
import com.lrc.missionO2.DTO.Response.AuthenticationResponse;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<MiscResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        authenticationService.register(registerRequest);
        return ResponseEntity.ok(MiscResponse.builder().response("User Registered, OTP sent").build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authenticationRequest){

        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

}
