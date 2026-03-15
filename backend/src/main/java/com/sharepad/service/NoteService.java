package com.sharepad.service;

import com.sharepad.model.Note;
import com.sharepad.model.User;
import com.sharepad.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note getOrCreateNote(String noteKey) {
        Optional<Note> optionalNote = noteRepository.findByNoteKey(noteKey);
        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            if (note.getExpiresAt() != null && note.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Note has expired");
            }
            return note;
        }

        Note note = new Note();
        note.setNoteKey(noteKey);
        note.setContent("");
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateContent(String noteKey, String content, User user) {
        Note note = getOrCreateNote(noteKey);
        note.setContent(content);
        return noteRepository.save(note);
    }

}
