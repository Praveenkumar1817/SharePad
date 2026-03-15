package com.sharepad.repository;

import com.sharepad.model.Note;
import com.sharepad.model.NoteLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteLockRepository extends JpaRepository<NoteLock, Long> {
    Optional<NoteLock> findByNote(Note note);

    List<NoteLock> findByLockedUntilBefore(LocalDateTime time);
}
