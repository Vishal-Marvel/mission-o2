package com.lrc.missionO2.services;

import com.lrc.missionO2.DTO.Request.*;
import com.lrc.missionO2.DTO.Response.*;
import com.lrc.missionO2.entity.FileData;
import com.lrc.missionO2.entity.Order;
import com.lrc.missionO2.entity.User;
import com.lrc.missionO2.entity.UserRole;
import com.lrc.missionO2.entity.addressList.District;
import com.lrc.missionO2.entity.addressList.State;
import com.lrc.missionO2.exceptions.ConstraintException;
import com.lrc.missionO2.exceptions.ItemNotFoundException;
import com.lrc.missionO2.exceptions.UserNotFoundException;
import com.lrc.missionO2.repository.Address.DistrictRepo;
import com.lrc.missionO2.repository.Address.StateRepo;
import com.lrc.missionO2.repository.FileRepo;
import com.lrc.missionO2.repository.UserRepo;
import com.lrc.missionO2.security.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
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
    private static final String CHARACTERS = "0123456789";
    private static final int ID_LENGTH = 6;

    public FileResponse getProof(String id){
        FileData file = fileRepo.findById(id).orElseThrow(()->new ItemNotFoundException("File: " + id + "not Found"));
        return FileResponse.builder().image(file.getData()).fileName(file.getFileName()).build();
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
            smsService.sendSms(user.getOTP(), registerRequest.getMobile());

            userRepo.save(user);
            return "User Registered";
        }
        else{
            User user = optionalUser.get();
            user.setOTP(generateUniqueID());
            user.setOTPLimit(new Date(new Date().getTime() + 1000*60*60));
            smsService.sendSms(user.getOTP(), registerRequest.getMobile());
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

    public String setProfile(SetProfileRequest setProfileRequest) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findById(id)
                .orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setEmail(setProfileRequest.getEmail());
        user.setUsername(setProfileRequest.getName());
        user.setDob(setProfileRequest.getDob());
        user.setAddress(setProfileRequest.getAddress());
//        FileData file = new FileData();
//        file.setData(setProfileRequest.getFile().getBytes());
//        file.setFileName(setProfileRequest.getFile().getOriginalFilename());
//        fileRepo.save(file);
//        user.setProof(file.getId());
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
        return getViewProfileResponse(user);

    }

    private ViewProfileResponse getViewProfileResponse(User user) {
        List<Order> userOrders = orderService.getOrders(
                user.getId(), null, null, null, null, null, null, null
        );

        return ViewProfileResponse.builder()
                .id(user.getId())
                .name(user.getUsername())
                .address(user.getAddress())
                .email(user.getEmail())
                .dob(user.getDob())
//                .proof(user.getProof())
                .mobile(user.getMobile())
                .totalOrders((long) userOrders.size())
                .totalPlants(userOrders.stream().mapToLong(
                        Order::getTotalPlants
                ).sum())
                .build();
    }

    public String verifyProfile(String userId) {
        User user = userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setVerified(true);
        userRepo.save(user);
        return "User Verified";
    }

    public PaginatedResponse<ViewProfileResponse> viewUsers(Integer offSet, Integer size, String state, String district, String taluk, Integer plantCount, Integer orderCount){
        Pageable pageable = PageRequest.of(offSet, size);
        PaginatedResponse<ViewProfileResponse> response = new PaginatedResponse<>();
        List<ViewProfileResponse> users = getUsers(state, district, taluk, plantCount, orderCount);
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

    public List<ViewProfileResponse> getUsers(String state, String district, String taluk, Integer plantCount, Integer orderCount) {

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
                .map(this::getViewProfileResponse
                )
                .filter(viewProfileResponse -> {
                    if (plantCount!=null){
                        return viewProfileResponse.getTotalPlants() >= plantCount;
                    }
                    return true;
                }).filter(viewProfileResponse -> {
                    if (orderCount!=null){
                        return viewProfileResponse.getTotalOrders() >= orderCount;
                    }
                    return true;
                }).filter(viewProfileResponse -> {
                    if (taluk!=null){
                        return viewProfileResponse.getAddress().getTaluk().contains(taluk);
                    }
                    return true;
                })
                .toList();
    }

    public ByteArrayResource downloadUsers(
            String state, String district, String taluk, Integer plants, Integer orders, String type
    ) {
        try {
            String[] heads = {"User Name", "State", "District", "Taluk", "Total Plants", "Total Orders"};
            List<ViewProfileResponse> users = getUsers(state, district, taluk, plants, orders);


            if (Objects.equals(type, "XLS")) {
                return fileService.createUsersExcel(heads, users, state, district, taluk, plants, orders);
            } else if (Objects.equals(type, "PDF")) {
                return fileService.createUsersPDF(heads, users ,state, district, taluk, plants, orders);
            } else {
                return null;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createUser(UserCreationRequest authenticationRequest) {
        if (userRepo.findByEmail(authenticationRequest.getEmail()).isPresent()){
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

    public String changePassword(ChangePasswordRequest changePasswordRequest){
        User user = userRepo.findById(changePasswordRequest.getId())
            .orElseThrow(() -> new UserNotFoundException("User Not found"));
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        userRepo.save(user);
        return "User Saved";
    }

    public List<AdminProfileResponse> getAdminUsers() {
        List<User> users = userRepo.findAllByRole(UserRole.ROLE_ADMIN_ASSIST);
        return users.stream().map(user ->
                AdminProfileResponse.builder()
                        .name(user.getUsername())
                        .id(user.getId())
                        .email(user.getEmail())
                        .build())
                .toList();

    }

    public String deleteUser(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not found"));
        userRepo.deleteById(userId);
        return "User Saved";
    }
}
