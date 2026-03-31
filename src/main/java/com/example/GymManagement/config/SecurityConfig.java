package com.example.GymManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // ================= PUBLIC PAGES =================
                .requestMatchers(
                        "/",
                        "/home",
                        "/login",
                        "/register",
                        "/forgot-password",
                        "/send-otp",
                        "/reset-password",
                        "/verify-otp",
                        "/about",
                        "/contact",
                        "/trainers",
                        "/membership",
                        "/store",              
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**"
                ).permitAll()

                // ================= ADMIN PAGES =================
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ================= USER / AUTH REQUIRED =================
                .requestMatchers(
                        "/profile",
                        "/edit-profile",
                        "/cart",
                        "/checkout",
                        "/buy-now/**",        
                        "/add-to-cart/**",    
                        "/store/order/**",
                        "/store/payment/**"
                ).hasAnyRole("USER", "ADMIN")

                // ================= ANY OTHER REQUEST =================
                .anyRequest().authenticated()
            )

            // ================= LOGIN =================
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/do-login")
                    .defaultSuccessUrl("/post-login", true)
                    .permitAll()
            )

            // ================= LOGOUT =================
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            )

            // ================= ACCESS DENIED =================
            .exceptionHandling(ex -> ex
                    .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    // ================= PASSWORD ENCODER =================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
