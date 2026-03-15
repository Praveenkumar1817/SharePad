package com.sharepad.dto;

import java.time.LocalDateTime;

public class NoteResponse {
    private String noteKey;
    private String content;
    private boolean locked;
    private String lockedByName;
    private String lockedByEmail;
    private LocalDateTime lockedUntil;

    public NoteResponse(String noteKey, String content, boolean locked, String lockedByName,
            String lockedByEmail, LocalDateTime lockedUntil) {
        this.noteKey = noteKey;
        this.content = content;
        this.locked = locked;
        this.lockedByName = lockedByName;
        this.lockedByEmail = lockedByEmail;
        this.lockedUntil = lockedUntil;
    }

    public String getNoteKey() { return noteKey; }
    public void setNoteKey(String noteKey) { this.noteKey = noteKey; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public String getLockedByName() { return lockedByName; }
    public void setLockedByName(String lockedByName) { this.lockedByName = lockedByName; }

    public String getLockedByEmail() { return lockedByEmail; }
    public void setLockedByEmail(String lockedByEmail) { this.lockedByEmail = lockedByEmail; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
}
