package com.lrc.missionO2.controller;

import com.lrc.missionO2.DTO.Request.*;
import com.lrc.missionO2.DTO.Response.*;
import com.lrc.missionO2.security.JWTTokenProvider;
import com.lrc.missionO2.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping("/register-user-mobile")
    public ResponseEntity<MiscResponse> registerViaMobile(@Valid @RequestBody RegisterRequestViaMobile registerRequestViaMobile){
       String response = userService.registerViaAppViaMobile(registerRequestViaMobile);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }
    @PostMapping("/register-user-email")
    public ResponseEntity<AuthenticationResponse> registerViaEmail(@Valid @RequestBody RegisterRequestViaEmail registerRequestViaEmail){
        return ResponseEntity.ok(userService.registerViaAppViaEmail(registerRequestViaEmail));
    }
    @PostMapping("/verify-email/{code}")
    public ResponseEntity<MiscResponse> verifyEmail(@PathVariable String code){
        String response = userService.verifyEmail(code);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }


    @PostMapping("/login-portal")
    public ResponseEntity<AuthenticationResponse> loginToPortal(@Valid @RequestBody LoginRequestWithEmail loginRequestWithEmail){
        return ResponseEntity.ok(userService.authenticateWithEmail(loginRequestWithEmail, false));
    }
    @PostMapping("/login-user-mobile")
    public ResponseEntity<AuthenticationResponse> loginToAppViaMobile(@Valid @RequestBody AppLoginRequestViaMobile appLoginRequestViaMobile){
            return ResponseEntity.ok(userService.authenticateViaAppUsingOTP(appLoginRequestViaMobile));
    }

    @PostMapping("/login-user-email")
    public ResponseEntity<AuthenticationResponse> loginToAppViaEmail(@Valid @RequestBody LoginRequestWithEmail loginRequestWithEmail){
            return ResponseEntity.ok(userService.authenticateWithEmail(loginRequestWithEmail, true));
    }

    @PostMapping("/dynamic-verify")
    public ResponseEntity<MiscResponse> dynamicVerify(@RequestBody ResetPasswordRequest resetPasswordRequest) throws MessagingException {
        userService.requestVerificationMail(resetPasswordRequest, false);
        return ResponseEntity.ok(MiscResponse.builder()
                .response("Verification Link Sent").build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MiscResponse> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordRequest,
                                                      @RequestParam("code") String code){
        String response = userService.resetPassword(code, resetPasswordRequest);
        return ResponseEntity.ok(MiscResponse.builder()
                .response(response).build());
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<MiscResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        String response = userService.requestVerificationMail(resetPasswordRequest, true);
        return ResponseEntity.ok(MiscResponse.builder()
                .response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_ADMIN_ASSIST')")
    @PutMapping("/update-profile")
    public ResponseEntity<MiscResponse> setProfile(@RequestBody @Valid SetProfileRequest setProfileRequest)  {

//        if (bindingResult.hasErrors()){
//            throw new InvalidArgumentException(bindingResult.getFieldErrors());
//        }
        String response = userService.setProfile(setProfileRequest);
        return ResponseEntity.ok(MiscResponse.builder().response(response).build());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/view-profile")
    public ResponseEntity<ViewProfileResponse> viewProfile(
            @RequestParam(value = "user", required = false) String user
    ){
        return ResponseEntity.ok(userService.viewCustomerProfile(user));
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
    public ResponseEntity<PaginatedResponse<ViewProfileResponse>> customers(
            @PathVariable Integer offSet, @PathVariable Integer size,
            @RequestParam(value = "state",required = false) String state,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "taluk", required = false) String taluk,
            @RequestParam(value = "plants", required = false) Integer plantPlantedCount,
            @RequestParam(value = "orders", required = false) Integer ordersCount
    ){
        return ResponseEntity.ok(userService.viewCustomers(offSet, size, state, district, taluk, plantPlantedCount, ordersCount));
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
    public ResponseEntity<Resource> downloadUsers(
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
                .body(userService.downloadCustomers(state, district, taluk, plantPlantedCount, ordersCount, type));
    }
}
