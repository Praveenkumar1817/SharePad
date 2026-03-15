package com.sharepad.service;

import com.sharepad.model.Note;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ExportService {

    public byte[] exportToTxt(Note note) {
        return note.getContent().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportToMarkdown(Note note) {
        String md = "# Note: " + note.getNoteKey() + "\n\n" + note.getContent();
        return md.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportToPdf(Note note) {
        // Placeholder for PDF generation
        // Requires something like iText or PDFBox, for now just returns text
        String content = "PDF Export for " + note.getNoteKey() + "\n\n" + note.getContent();
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
