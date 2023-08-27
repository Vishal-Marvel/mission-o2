package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.AppLoginRequest;
import com.lrc.missionO2.DTO.Request.AuthenticationRequest;
import com.lrc.missionO2.DTO.Request.RegisterRequest;
import com.lrc.missionO2.DTO.Request.SetProfileRequest;
import com.lrc.missionO2.DTO.Response.AuthenticationResponse;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.DTO.Response.ViewProfileResponse;
import com.lrc.missionO2.security.JWTTokenProvider;
import com.lrc.missionO2.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping("/register-user")
    public ResponseEntity<MiscResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
       String response = userService.registerViaApp(registerRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @PostMapping("/login-portal")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(userService.authenticatePortal(authenticationRequest));
    }
    @PostMapping("/login-user")
    public ResponseEntity<AuthenticationResponse> loginUser(@Valid @RequestBody AppLoginRequest appLoginRequest){
            return ResponseEntity.ok(userService.authenticateApp(appLoginRequest));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_UER', 'ROLE_ADMIN')")
    @PutMapping("/update-profile")
    public ResponseEntity<MiscResponse> setProfile(@Valid @ModelAttribute SetProfileRequest setProfileRequest) throws IOException {
        String response = userService.setProfile(setProfileRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/view-profile")
    public ResponseEntity<ViewProfileResponse> viewProfile(
            @RequestParam(value = "user", required = false) String user
    ){
        return ResponseEntity.ok(userService.viewProfile(user));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/verify-profile/{userId}")
    public ResponseEntity<MiscResponse> verifyProfile(
            @PathVariable String userId){
        String response = userService.verifyProfile(userId);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @GetMapping("/isActive/{token}")
    public ResponseEntity<MiscResponse> isActive(@PathVariable String token){
        try{
            jwtTokenProvider.validateToken(token);
            return ResponseEntity.badRequest().body(MiscResponse.builder().response("Active").build());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(MiscResponse.builder().response("InActive").build());
        }
    }
}
