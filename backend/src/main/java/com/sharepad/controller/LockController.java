package com.sharepad.controller;

import com.sharepad.model.User;
import com.sharepad.service.AuthService;
import com.sharepad.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lock")
public class LockController {

    @Autowired
    private LockService lockService;

    @Autowired
    private AuthService authService;

    @PostMapping("/{noteKey}")
    public ResponseEntity<?> lockNote(@PathVariable String noteKey,
            OAuth2AuthenticationToken token) {
        if (token == null)
            return ResponseEntity.status(401).build();
        User user = authService.getOrCreateUser(token);
        try {
            boolean success = lockService.lockNote(noteKey, user);
            if (success) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(409).body(Map.of("error", "Note is already locked by another user"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(Map.of("error", "Note is already locked by another user"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An error occurred while locking the note"));
        }
    }

    @PostMapping("/{noteKey}/extend")
    public ResponseEntity<?> extendLock(@PathVariable String noteKey,
            OAuth2AuthenticationToken token) {
        if (token == null)
            return ResponseEntity.status(401).build();
        User user = authService.getOrCreateUser(token);
        try {
            boolean success = lockService.extendLock(noteKey, user);
            if (success)
                return ResponseEntity.ok().build();
            return ResponseEntity.badRequest().body(Map.of("error", "Could not extend lock"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{noteKey}/unlock")
    public ResponseEntity<?> unlockNote(@PathVariable String noteKey,
            OAuth2AuthenticationToken token) {
        if (token == null)
            return ResponseEntity.status(401).build();
        User user = authService.getOrCreateUser(token);
        lockService.unlockNote(noteKey, user);
        return ResponseEntity.ok().build();
    }
}
