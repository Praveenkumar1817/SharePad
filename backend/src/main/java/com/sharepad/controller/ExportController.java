package com.sharepad.controller;

import com.sharepad.model.Note;
import com.sharepad.service.ExportService;
import com.sharepad.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private NoteService noteService;

    @GetMapping("/{noteKey}")
    public ResponseEntity<byte[]> exportNote(@PathVariable String noteKey, @RequestParam String format) {
        Note note = noteService.getOrCreateNote(noteKey);
        byte[] content;
        String ext;
        MediaType mediaType;

        switch (format.toLowerCase()) {
            case "markdown":
            case "md":
                content = exportService.exportToMarkdown(note);
                ext = ".md";
                mediaType = MediaType.TEXT_MARKDOWN;
                break;
            case "pdf":
                content = exportService.exportToPdf(note);
                ext = ".pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case "txt":
            default:
                content = exportService.exportToTxt(note);
                ext = ".txt";
                mediaType = MediaType.TEXT_PLAIN;
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"note_" + noteKey + ext + "\"")
                .contentType(mediaType)
                .body(content);
    }
}
