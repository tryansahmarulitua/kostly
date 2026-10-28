package com.kostly.config;
import com.kostly.entity.User;
import com.kostly.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class DataInitializer {
    @Bean CommandLineRunner initializeAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                User user = new User();
                user.setUsername("admin");
                user.setPassword(encoder.encode("admin123"));
                user.setRole("ADMIN");
                users.save(user);
            }
        };
    }
}
