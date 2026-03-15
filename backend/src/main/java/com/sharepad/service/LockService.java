package com.sharepad.service;

import com.sharepad.model.Note;
import com.sharepad.model.NoteLock;
import com.sharepad.model.User;
import com.sharepad.repository.NoteLockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LockService {

    @Autowired
    private NoteLockRepository lockRepository;

    @Autowired
    private NoteService noteService;

    @Transactional
    public boolean lockNote(String noteKey, User user) {
        Note note = noteService.getOrCreateNote(noteKey);
        Optional<NoteLock> existingLock = lockRepository.findByNote(note);

        if (existingLock.isPresent()) {
            NoteLock lock = existingLock.get();
            if (lock.getLockedUntil().isAfter(LocalDateTime.now())) {
                return lock.getLockedBy().getId().equals(user.getId());
            } else {
                lockRepository.delete(lock); // Expired lock
            }
        }

        NoteLock newLock = new NoteLock();
        newLock.setNote(note);
        newLock.setLockedBy(user);
        newLock.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        newLock.setTotalLockMinutes(30);
        lockRepository.save(newLock);
        return true;
    }

    @Transactional
    public boolean extendLock(String noteKey, User user) {
        Note note = noteService.getOrCreateNote(noteKey);
        Optional<NoteLock> existingLock = lockRepository.findByNote(note);

        if (existingLock.isPresent()) {
            NoteLock lock = existingLock.get();
            if (lock.getLockedBy().getId().equals(user.getId())) {
                if (lock.getTotalLockMinutes() + 15 <= 60) {
                    lock.setLockedUntil(lock.getLockedUntil().plusMinutes(15));
                    lock.setTotalLockMinutes(lock.getTotalLockMinutes() + 15);
                    lockRepository.save(lock);
                    return true;
                } else {
                    throw new RuntimeException("Maximum lock duration reached (60 mins)");
                }
            }
        }
        return false;
    }

    @Transactional
    public void unlockNote(String noteKey, User user) {
        Note note = noteService.getOrCreateNote(noteKey);
        Optional<NoteLock> existingLock = lockRepository.findByNote(note);
        existingLock.ifPresent(lock -> {
            if (lock.getLockedBy().getId().equals(user.getId())) {
                lockRepository.delete(lock);
            }
        });
    }

    public Optional<NoteLock> getActiveLock(Note note) {
        Optional<NoteLock> existingLock = lockRepository.findByNote(note);
        if (existingLock.isPresent() && existingLock.get().getLockedUntil().isAfter(LocalDateTime.now())) {
            return existingLock;
        }
        return Optional.empty();
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void cleanupExpiredLocks() {
        List<NoteLock> expiredLocks = lockRepository.findByLockedUntilBefore(LocalDateTime.now());
        lockRepository.deleteAll(expiredLocks);
    }
}
