package com.lrc.missionO2.repository;

import com.lrc.missionO2.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
}
