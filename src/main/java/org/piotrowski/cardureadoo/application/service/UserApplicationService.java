package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.domain.security.UserRole;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.UserEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity createUser(String username, String rawPassword, Set<UserRole> roles) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hash = passwordEncoder.encode(rawPassword); // Argon2
        UserEntity user = new UserEntity(username, hash, roles == null || roles.isEmpty()
                ? EnumSet.of(UserRole.USER) : EnumSet.copyOf(roles));
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(String username, String oldRawPassword, String newRawPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldRawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password does not match");
        }

        String newHash = passwordEncoder.encode(newRawPassword);
        user.setPasswordHash(newHash);
    }
}
