package com.sharepad.controller;

import com.sharepad.dto.LoginResponse;
import com.sharepad.model.User;
import com.sharepad.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(OAuth2AuthenticationToken token) {
        if (token == null) {
            return ResponseEntity.ok(new LoginResponse("/oauth2/authorization/google", null, null, false));
        }
        User user = authService.getOrCreateUser(token);
        return ResponseEntity.ok(new LoginResponse(null, user.getEmail(), user.getName(), true));
    }
}
