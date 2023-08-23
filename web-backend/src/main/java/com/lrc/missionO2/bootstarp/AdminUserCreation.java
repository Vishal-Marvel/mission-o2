package com.lrc.missionO2.bootstarp;

import com.lrc.missionO2.entity.User;
import com.lrc.missionO2.entity.UserRole;
import com.lrc.missionO2.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminUserCreation implements CommandLineRunner {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepo.existsByUsername("admin")){
            User user = new User();
            user.setUsername("admin");
            user.setRole(UserRole.ROLE_ADMIN);
            user.setEmail("admin@lrc.com");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setVerified(true);
            userRepo.save(user);
        }

    }
}
