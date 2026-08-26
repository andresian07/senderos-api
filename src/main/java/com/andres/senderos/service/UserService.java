package com.andres.senderos.service;

import com.andres.senderos.config.CustomUserDetails;
import com.andres.senderos.persistence.entity.UserEntity;
import com.andres.senderos.persistence.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("usuario no encontrado: " + username));

        return new CustomUserDetails(user);

    }
}