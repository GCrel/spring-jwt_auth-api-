package com.github.GCrel.web.config;

import application.*;
import com.github.GCrel.data.jpa.IJPAUserRepository;
import com.github.GCrel.data.models.UserEntity;
import com.github.GCrel.web.services.JWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import port.input.*;
import port.output.IUserRepository;

@Configuration
public class AppConfig {

    private final IJPAUserRepository jpaUserRepository;

    public AppConfig(IJPAUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    // This class is used to define beans for the application context.
    @Bean
    public IGetAllUsersInput getAllUsersInput(IUserRepository userRepository) {
        return new GetAllUsers(userRepository);
    }

    @Bean
    public IGetUserByIdInput getUserInput(IUserRepository userRepository) {
        return new GetUserById(userRepository);
    }

    @Bean
    public ILoginUserInput getLoginUserInput(IUserRepository userRepository) {
        return new LoginUser(userRepository);
    }

    @Bean
    public IRegisterUserInput getRegisterUserInput(IUserRepository userRepository) {
        return new application.RegisterUser(userRepository);
    }

    @Bean
    public IDeleteUserInput getDeleteUserInput(IUserRepository userRepository) {
        return new DeteleUser(userRepository);
    }

    @Bean
    public IUpdateUserInput getUpdateUserInput(IUserRepository userRepository) {
        return new UpdateUser(userRepository);
    }

    // Define a PasswordEncoder bean for password hashing
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define a JWTService bean for handling JWT operations
    @Bean
    public JWTService getJWTService() {
        return new JWTService();
    }

    // Authentication and Authorization Configuration
    @Bean
    public UserDetailsService getUserDetailsService() {
        return username -> {
            UserEntity user = jpaUserRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider getAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(getUserDetailsService());
        provider.setPasswordEncoder(getPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
