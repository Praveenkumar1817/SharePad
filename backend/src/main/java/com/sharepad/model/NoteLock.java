package com.sharepad.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_locks")
public class NoteLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne
    @JoinColumn(name = "locked_by", nullable = false)
    private User lockedBy;

    @Column(name = "locked_until", nullable = false)
    private LocalDateTime lockedUntil;

    @Column(name = "total_lock_minutes", nullable = false)
    private int totalLockMinutes = 0;

    public NoteLock() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public User getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(User lockedBy) {
        this.lockedBy = lockedBy;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public int getTotalLockMinutes() {
        return totalLockMinutes;
    }

    public void setTotalLockMinutes(int totalLockMinutes) {
        this.totalLockMinutes = totalLockMinutes;
    }
}
