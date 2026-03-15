package com.sharepad.controller;

import com.sharepad.dto.NoteResponse;
import com.sharepad.model.Note;
import com.sharepad.model.NoteLock;

import com.sharepad.service.LockService;
import com.sharepad.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private LockService lockService;


    @GetMapping("/{noteKey}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable String noteKey) {
        Note note = noteService.getOrCreateNote(noteKey);
        Optional<NoteLock> lock = lockService.getActiveLock(note);

        boolean isLocked = lock.isPresent();
        String lockedByEmail = isLocked ? lock.get().getLockedBy().getEmail() : null;
        String lockedByName = isLocked ? maskEmail(lockedByEmail) : null;
        Long lockedUntilMillis = isLocked ? lock.get().getLockedUntil().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null;

        return ResponseEntity.ok(new NoteResponse(
                note.getNoteKey(),
                note.getContent(),
                isLocked,
                lockedByName,
                lockedByEmail,
                lockedUntilMillis));
    }

    private String maskEmail(String email) {
        if (email == null) return null;
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return email;
        int visibleChars = Math.min(4, atIndex - 1);
        String prefix = email.substring(0, visibleChars);
        String domain = email.substring(atIndex);
        return prefix + "***" + domain;
    }

}
