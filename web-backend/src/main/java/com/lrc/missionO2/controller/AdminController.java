package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.ChangePasswordRequest;
import com.lrc.missionO2.DTO.Request.UserCreationRequest;
import com.lrc.missionO2.DTO.Response.AdminProfileResponse;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.services.SMSService;
import com.lrc.missionO2.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {
    private final SMSService smsService;
    private final UserService userService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/sms-balance")
    public ResponseEntity<MiscResponse> getSmsBalance(){
        String response = smsService.checkBalance();
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<MiscResponse> createUser(@RequestBody UserCreationRequest authenticationRequest){
        String response = userService.createUser(authenticationRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-users")
    public ResponseEntity<List<AdminProfileResponse>> getAdminProfile(){

        return ResponseEntity.ok(userService.getAdminUsers());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/change-user-password")
    public ResponseEntity<MiscResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        String response = userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<MiscResponse> deleteUser(@PathVariable String userId){
        String response = userService.deleteUser(userId);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

}
