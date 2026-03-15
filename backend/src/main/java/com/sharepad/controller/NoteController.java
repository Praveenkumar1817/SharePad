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
        String lockedByName = isLocked ? lock.get().getLockedBy().getName() : null;
        java.time.LocalDateTime lockedUntil = isLocked ? lock.get().getLockedUntil() : null;

        return ResponseEntity.ok(new NoteResponse(
                note.getNoteKey(),
                note.getContent(),
                isLocked,
                lockedByName,
                lockedUntil));
    }

}
