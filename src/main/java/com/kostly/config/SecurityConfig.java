package com.kostly.config;
import com.kostly.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsername(username)
          .map(user -> User.withUsername(user.getUsername()).password(user.getPassword())
             .roles(user.getRole()).build())
          .orElseThrow(() -> new UsernameNotFoundException("Pengguna tidak ditemukan"));
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
             .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
             .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }
}
