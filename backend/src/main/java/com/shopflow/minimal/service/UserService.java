package com.shopflow.minimal.service;

import com.shopflow.minimal.entity.Role;
import com.shopflow.minimal.entity.User;
import com.shopflow.minimal.exception.ResourceNotFoundException;
import com.shopflow.minimal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isAdmin(String email) {
        return getByEmail(email).getRole() == Role.ADMIN;
    }
}
