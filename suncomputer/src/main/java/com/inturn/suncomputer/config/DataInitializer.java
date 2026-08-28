package com.inturn.suncomputer.config;

import com.inturn.suncomputer.user.entity.Role;
import com.inturn.suncomputer.user.entity.RoleName;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.RoleRepository;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            for (RoleName roleName : RoleName.values()) {

                roleRepository
                        .findByName(roleName)
                        .orElseGet(() ->
                                roleRepository.save(
                                        new Role(roleName)
                                )
                        );
            }

            if (
                    userRepository
                            .findByUsername("admin")
                            .isEmpty()
            ) {

                Role adminRole =
                        roleRepository
                                .findByName(
                                        RoleName.ADMIN
                                )
                                .orElseThrow();

                User admin = new User();

                admin.setUsername("admin");

                admin.setEmail(
                        "admin@inturn.local"
                );

                admin.setPassword(
                        passwordEncoder.encode(
                                "Admin@123"
                        )
                );

                admin.setFirstName("System");

                admin.setLastName("Administrator");

                admin.setEnabled(true);

                admin.setRoles(
                        new HashSet<>(
                                java.util.Set.of(
                                        adminRole
                                )
                        )
                );

                userRepository.save(admin);
            }
        };
    }
}
