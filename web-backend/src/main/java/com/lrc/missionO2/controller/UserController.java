package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.AppLoginRequest;
import com.lrc.missionO2.DTO.Request.AuthenticationRequest;
import com.lrc.missionO2.DTO.Request.RegisterRequest;
import com.lrc.missionO2.DTO.Request.SetProfileRequest;
import com.lrc.missionO2.DTO.Response.*;
import com.lrc.missionO2.security.JWTTokenProvider;
import com.lrc.missionO2.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

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
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
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

//    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
//    @GetMapping("/view-profile-file/{id}")
//    public ResponseEntity<FileResponse> viewFile(@PathVariable String id){
//        return ResponseEntity.ok(userService.getProof(id));
//    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/verify-profile/{userId}")
    public ResponseEntity<MiscResponse> verifyProfile(
            @PathVariable String userId){
        String response = userService.verifyProfile(userId);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/view/{offSet}/{size}")
    public ResponseEntity<PaginatedResponse<ViewProfileResponse>> users(
            @PathVariable Integer offSet, @PathVariable Integer size,
            @RequestParam(value = "state",required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "plants", required = false) Integer plantPlantedCount,
            @RequestParam(value = "orders", required = false) Integer ordersCount
    ){
        return ResponseEntity.ok(userService.viewUsers(offSet, size, state, district, taluk, plantPlantedCount, ordersCount));
    }

    @GetMapping("/isActive/{token}")
    public ResponseEntity<MiscResponse> isActive(@PathVariable String token){
        try{
            jwtTokenProvider.validateToken(token);
            return ResponseEntity.ok().body(MiscResponse.builder().response("Active").build());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(MiscResponse.builder().response("InActive").build());
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/download")
    public ResponseEntity<Resource> download(
            @RequestParam(value = "state",required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "plants", required = false) Integer plantPlantedCount,
            @RequestParam(value = "orders", required = false) Integer ordersCount,
            @RequestParam(value = "type") String type
    ){
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = null;
        if (Objects.equals(type, "XLS")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Users.xlsx");
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }else if (Objects.equals(type, "PDF")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Users.pdf");
            mediaType = MediaType.APPLICATION_PDF;
        }
        assert mediaType != null;
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(userService.downloadUsers(state, district, taluk, plantPlantedCount, ordersCount, type));
    }
}
