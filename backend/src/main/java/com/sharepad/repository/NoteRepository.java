package com.sharepad.repository;

import com.sharepad.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByNoteKey(String noteKey);

    List<Note> findByExpiresAtBefore(LocalDateTime time);
}
