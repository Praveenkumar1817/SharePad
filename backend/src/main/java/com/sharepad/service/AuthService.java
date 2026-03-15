package com.sharepad.service;

import com.sharepad.model.User;
import com.sharepad.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User getOrCreateUser(OAuth2AuthenticationToken token) {
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("name");
        String providerId = token.getPrincipal().getName();
        String provider = token.getAuthorizedClientRegistrationId();

        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        User newUser = new User(java.util.UUID.randomUUID().toString(), email, name, provider, providerId);
        return userRepository.save(newUser);
    }
}
