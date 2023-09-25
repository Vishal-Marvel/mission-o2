package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.*;
import com.lrc.missionO2.DTO.Response.*;
import com.lrc.missionO2.entity.FileData;
import com.lrc.missionO2.entity.Order;
import com.lrc.missionO2.entity.User;
import com.lrc.missionO2.entity.UserRole;
import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.exceptions.APIException;
import com.lrc.missionO2.exceptions.ConstraintException;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.exceptions.UserNotFoundException;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.UserRepo;
import com.lrc.missionO2.security.JWTTokenProvider;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String CHARACTERS = "0123456789";
    private static final int ID_LENGTH = 6;
    private final UserRepo userRepo;
    private final FileRepo fileRepo;
    private final JWTTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SMSService smsService;
    private final StateRepo stateRepo;
    private final FileService fileService;
    private final DistrictRepo districtRepo;
    private final OrderService orderService;
    private final MongoOperations mongoOperations;
    private final EmailService emailService;

    public static boolean onlyDigits(String str) {

        for (int i = 0; i < str.length(); i++) {

            if (!Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
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

    public FileResponse getProof(String id) {
        FileData file = fileRepo.findById(id).orElseThrow(() -> new ItemNotFoundException("File: " + id + "not Found"));
        return FileResponse.builder().image(file.getData()).fileName(file.getFileName()).build();
    }

    public String registerViaAppViaMobile(RegisterRequestViaMobile registerRequestViaMobile) {
        Optional<User> optionalUser = userRepo.findByMobile(registerRequestViaMobile.getMobile());
        if (onlyDigits(registerRequestViaMobile.getMobile())){
            throw new ConstraintException("Mobile Number should not contain characters");
        }

        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setMobile(registerRequestViaMobile.getMobile());
            user.setPassword(passwordEncoder.encode(registerRequestViaMobile.getMobile()));
            user.setRole(UserRole.ROLE_USER);
            user.setVerified(false);
            user.setOTP(generateUniqueID());
            user.setOTPLimit(new Date(new Date().getTime() + 1000 * 60 * 60));
            smsService.sendSms(user.getOTP(), registerRequestViaMobile.getMobile());

            userRepo.save(user);
            return "User Registered";
        } else {
            User user = optionalUser.get();
            user.setOTP(generateUniqueID());
            user.setOTPLimit(new Date(new Date().getTime() + 1000 * 60 * 60));
            smsService.sendSms(user.getOTP(), registerRequestViaMobile.getMobile());
            userRepo.save(user);
            return "OTP Sent";
        }
    }

    public AuthenticationResponse registerViaAppViaEmail(RegisterRequestViaEmail registerRequestViaEmail) {
        try {
            if (userRepo.findByEmail(registerRequestViaEmail.getEmail()).isPresent()) {
                throw new ConstraintException("Email already in use");
            }
            if (userRepo.findByMobile(registerRequestViaEmail.getMobile()).isPresent()) {
                throw new ConstraintException("Mobile number already in use");
            }
            if (onlyDigits(registerRequestViaEmail.getMobile())){
                throw new ConstraintException("Mobile Number should not contain characters");
            }
            if (onlyDigits(registerRequestViaEmail.getAddress().getPinCode())){
                throw new ConstraintException("Pin Code should not contain characters");
            }
            User user = new User();
            user.setRole(UserRole.ROLE_USER);
            user.setEmail(registerRequestViaEmail.getEmail());
            user.setMobile(registerRequestViaEmail.getMobile());
            user.setDob(registerRequestViaEmail.getDob());
            user.setUsername(registerRequestViaEmail.getName());
            user.setPassword(passwordEncoder.encode(registerRequestViaEmail.getPassword()));
            user.setAddress(registerRequestViaEmail.getAddress());
            user.setGender(registerRequestViaEmail.getGender());
            user.setVerified(true);

            emailService.sendWelcomeMail(registerRequestViaEmail.getEmail());
            userRepo.save(user);

            Authentication auth = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword());
            return AuthenticationResponse.builder()
                    .token(jwtTokenProvider
                            .generateToken(auth, true))
                    .role(user.getRole().name())
                    .build();

        } catch (MessagingException messagingException) {
            throw new APIException(messagingException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AuthenticationResponse authenticateWithEmail(LoginRequestWithEmail loginRequestWithEmail, boolean isApp) {
        User user = userRepo.findByEmail(loginRequestWithEmail.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User Not found"));

        if (!user.isVerified()){
            throw new ConstraintException("User Not Verified");
        }
        if (!passwordEncoder.matches(loginRequestWithEmail.getPassword(), user.getPassword())) {
            throw new AccessDeniedException("Bad Credentials");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword());
        return AuthenticationResponse.builder()
                .token(jwtTokenProvider
                        .generateToken(auth, isApp))
                .role(user.getRole().name())
                .build();
    }

    public AuthenticationResponse authenticateViaAppUsingOTP(AppLoginRequestViaMobile appLoginRequestViaMobile) {

        User user = userRepo.findByMobile(appLoginRequestViaMobile.getMobile())
                .orElseThrow(() -> new UserNotFoundException("User Not found"));

        if (!Objects.equals(user.getOTP(), appLoginRequestViaMobile.getOtp()) || new Date().after(user.getOTPLimit())) {

            throw new AccessDeniedException("Invalid OTP");
        }

        user.setOTPLimit(null);
        user.setOTP(null);
        user.setVerified(true);
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword());

        userRepo.save(user);
        return AuthenticationResponse.builder()
                .token(jwtTokenProvider
                        .generateToken(auth, true))
                .role(user.getRole().name())
                .build();
    }

    public String verifyEmail(String otp) {
        User user = userRepo.findByOTP(otp).orElseThrow(() -> new UserNotFoundException("Invalid Code"));
        if (!Objects.equals(user.getOTP(), otp) || new Date().after(user.getOTPLimit())) {
            throw new AccessDeniedException("Invalid OTP");
        }
        user.setOTPLimit(null);
        user.setOTP(null);
        user.setVerified(true);
        userRepo.save(user);
        return "Email Verified";
    }


    public String resetPassword(String code, ResetPasswordDTO resetPasswordRequest) {
        User user = userRepo.findByOTP(code)
                .orElseThrow(() -> new UserNotFoundException("Invalid Code"));
        if (!Objects.equals(user.getOTP(), code) || new Date().after(user.getOTPLimit())) {
            throw new AccessDeniedException("Invalid Code");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setOTP(null);
        user.setOTPLimit(null);
        userRepo.save(user);
        return "Password Reset successful";
    }

    public String requestVerificationMail(ResetPasswordRequest resetPasswordRequest, boolean reset) {
        try {
            User user = userRepo.findByEmail(resetPasswordRequest.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User Not Found"));
            String otp = UUID.randomUUID().toString();
            user.setOTP(otp);
            user.setOTPLimit(new Date(new Date().getTime() + 1000 * 60 * 60));
            if (reset) {
                emailService.sendResetPasswordMail(resetPasswordRequest.getEmail(), otp);
            }
            else{
                emailService.sendVerificationEmail(resetPasswordRequest.getEmail(), otp);
            }
            userRepo.save(user);
            return "Reset Link Sent";
        } catch (MessagingException messagingException) {
            throw new APIException(messagingException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public String setProfile(SetProfileRequest setProfileRequest) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        user.setEmail(setProfileRequest.getEmail());
        if (onlyDigits(setProfileRequest.getMobile())){
            throw new ConstraintException("Mobile Number should not contain characters");
        }
        if (onlyDigits(setProfileRequest.getAddress().getPinCode())){
            throw new ConstraintException("Pin Code should not contain characters");
        }
        user.setMobile(setProfileRequest.getMobile());
        user.setUsername(setProfileRequest.getName());
        user.setDob(setProfileRequest.getDob());
        user.setGender(setProfileRequest.getGender());
        user.setAddress(setProfileRequest.getAddress());

        userRepo.save(user);
        return "User Profile Set";
    }


    public ViewProfileResponse viewCustomerProfile(String userId) {
        User user = null;
        if (Objects.isNull(userId)) {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            user = userRepo.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        } else {
            user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        }
        return covertCustomerDetailsToDTO(user);

    }

    private ViewProfileResponse covertCustomerDetailsToDTO(User user) {
        List<Order> userOrders = orderService.getOrders(
                user.getId(), null, null, null, null, null, null, null
        );

        return ViewProfileResponse.builder()
                .id(user.getId())
                .name(user.getUsername())
                .address(user.getAddress())
                .email(user.getEmail())
                .dob(user.getDob())
                .gender(user.getGender())
//                .proof(user.getProof())
                .mobile(user.getMobile())
                .totalOrders((long) userOrders.size())
                .totalPlants(userOrders.stream().mapToLong(
                        Order::getTotalPlants
                ).sum())
                .build();
    }

    public String verifyProfile(String userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        user.setVerified(true);
        userRepo.save(user);
        return "User Verified";
    }

    public PaginatedResponse<ViewProfileResponse> viewCustomers(Integer offSet, Integer size, String state, String district, String taluk, Integer plantCount, Integer orderCount) {
        Pageable pageable = PageRequest.of(offSet, size);
        PaginatedResponse<ViewProfileResponse> response = new PaginatedResponse<>();
        List<ViewProfileResponse> users = getCustomers(state, district, taluk, plantCount, orderCount);
        List<ViewProfileResponse> paginatedUsers = users.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        long totalCount = users.size();
        int totalPages = (int) Math.ceil((double) totalCount / size);
        response.setTotalPages(totalPages);
        response.setTotal(totalCount);
        response.setData(paginatedUsers);
        return response;
    }

    public List<ViewProfileResponse> getCustomers(String state, String district, String taluk, Integer plantCount, Integer orderCount) {

        Query query = new Query();
        if (state != null) {
            List<State> selectedState = stateRepo.findAllByStateNameLikeIgnoreCase(state);
            query.addCriteria(Criteria.where("address.state").in(selectedState));
        }
        if (district != null) {
            List<District> districts = districtRepo.findAllByDistrictNameLikeIgnoreCase(district);
            query.addCriteria(Criteria.where("address.state").in(districts));
        }
        query.addCriteria(Criteria.where("role").is(UserRole.ROLE_USER));
        List<User> users = mongoOperations.find(query, User.class);
        return users.stream()
                .map(this::covertCustomerDetailsToDTO
                )
                .filter(viewProfileResponse -> {
                    if (plantCount != null) {
                        return viewProfileResponse.getTotalPlants() >= plantCount;
                    }
                    return true;
                }).filter(viewProfileResponse -> {
                    if (orderCount != null) {
                        return viewProfileResponse.getTotalOrders() >= orderCount;
                    }
                    return true;
                }).filter(viewProfileResponse -> {
                    if (taluk != null) {
                        return viewProfileResponse.getAddress().getTaluk().contains(taluk);
                    }
                    return true;
                })
                .toList();
    }

    public ByteArrayResource downloadCustomers(
            String state, String district, String taluk, Integer plants, Integer orders, String type
    ) {
        try {
            String[] heads = {"User Name", "State", "District", "Taluk", "Total Plants", "Total Orders"};
            List<ViewProfileResponse> users = getCustomers(state, district, taluk, plants, orders);


            if (Objects.equals(type, "XLS")) {
                return fileService.createUsersExcel(heads, users, state, district, taluk, plants, orders);
            } else if (Objects.equals(type, "PDF")) {
                return fileService.createUsersPDF(heads, users, state, district, taluk, plants, orders);
            } else {
                return null;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createAdminAsstUser(UserCreationRequest authenticationRequest) {
        if (userRepo.findByEmail(authenticationRequest.getEmail()).isPresent()) {
            throw new ConstraintException("Email Already Exists");
        }
        User user = new User();
        user.setUsername(authenticationRequest.getName());
        user.setEmail(authenticationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
        user.setRole(UserRole.ROLE_ADMIN_ASSIST);
        user.setVerified(true);
        userRepo.save(user);
        return "User Created";
    }

    public String changePasswordOfAdminAsstUser(ChangeAdminAssistPasswordRequest changeAdminAssistPasswordRequest) {
        User user = userRepo.findById(changeAdminAssistPasswordRequest.getId())
                .orElseThrow(() -> new UserNotFoundException("User Not found"));
        user.setPassword(passwordEncoder.encode(changeAdminAssistPasswordRequest.getPassword()));
        userRepo.save(user);
        return "User Saved";
    }

    public List<AdminProfileResponse> getAdminAsstUsers() {
        List<User> users = userRepo.findAllByRole(UserRole.ROLE_ADMIN_ASSIST);
        return users.stream().map(user ->
                        AdminProfileResponse.builder()
                                .name(user.getUsername())
                                .id(user.getId())
                                .email(user.getEmail())
                                .build())
                .toList();

    }

    public String deleteAdminAsstUser(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not found"));
        userRepo.deleteById(userId);
        return "User Saved";
    }


}
