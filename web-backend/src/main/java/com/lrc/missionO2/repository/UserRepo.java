package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.User;
import com.lrc.missionO2.entity.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    List<User> findAllByRole(UserRole role);
    Optional<User> findByOTP(String otp);
}
