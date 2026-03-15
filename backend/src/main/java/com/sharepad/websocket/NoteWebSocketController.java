package com.sharepad.websocket;

import com.sharepad.model.User;
import com.sharepad.model.NoteLock;
import com.sharepad.repository.UserRepository;
import com.sharepad.service.LockService;
import com.sharepad.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class NoteWebSocketController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private LockService lockService;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/note/{noteKey}/edit")
    @SendTo("/topic/note/{noteKey}")
    public EditMessage processEditMessage(@DestinationVariable String noteKey,
            @Payload EditMessage message,
            OAuth2AuthenticationToken token) {
        if (token == null) {
            throw new RuntimeException("Unauthenticated user cannot edit");
        }

        String email = token.getPrincipal().getAttribute("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        // Check lock permissions
        com.sharepad.model.Note note = noteService.getOrCreateNote(noteKey);
        Optional<NoteLock> lock = lockService.getActiveLock(note);

        if (lock.isPresent() && !lock.get().getLockedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Note is locked by another user");
        }

        // Save note and broadcast
        noteService.updateContent(noteKey, message.getContent(), user);

        message.setSenderEmail(email);
        return message;
    }
}
