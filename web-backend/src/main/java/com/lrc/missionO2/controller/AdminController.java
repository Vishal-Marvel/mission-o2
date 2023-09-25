package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.ChangeAdminAssistPasswordRequest;
import com.lrc.missionO2.DTO.Request.UserCreationRequest;
import com.lrc.missionO2.DTO.Response.AdminProfileResponse;
import com.lrc.missionO2.DTO.Response.MiscResponse;
import com.lrc.missionO2.services.SMSService;
import com.lrc.missionO2.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
    public ResponseEntity<MiscResponse> createUser(@RequestBody @Valid UserCreationRequest authenticationRequest){
        String response = userService.createAdminAsstUser(authenticationRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-users")
    public ResponseEntity<List<AdminProfileResponse>> getAdminProfile(){

        return ResponseEntity.ok(userService.getAdminAsstUsers());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/change-user-password")
    public ResponseEntity<MiscResponse> changePassword(@RequestBody @Valid ChangeAdminAssistPasswordRequest changeAdminAssistPasswordRequest){
        String response = userService.changePasswordOfAdminAsstUser(changeAdminAssistPasswordRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<MiscResponse> deleteUser(@PathVariable String userId){
        String response = userService.deleteAdminAsstUser(userId);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());

    }

}
