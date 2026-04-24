package com.example.demo;

import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class DocumentOrchestrator {

    private final TextBasedPdfExtractor textExtractor;
    private final PdfOcrExample ocrProcessor;

    public DocumentOrchestrator(TextBasedPdfExtractor textExtractor, PdfOcrExample ocrProcessor) {
        this.textExtractor = textExtractor;
        this.ocrProcessor = ocrProcessor;
    }
    private String runOcrPipeline(File file, String bank) throws Exception {
        return ocrProcessor.extractText(file, bank);
    }
    private String runTextPipeline(File file) throws Exception {
        return textExtractor.extractText(file);
    }
    public String processDocument(File file, String bank) throws Exception {
        // Pasul 1: Verificăm dacă PDF-ul are text nativ
        if (textExtractor.isTextBased(file)) {
            System.out.println("Document digital detectat. Folosesc extragerea directă.");
            return runTextPipeline(file);
        } else {
            System.out.println("Document scanat detectat. Folosesc OCR.");
            return runOcrPipeline(file, bank);
        }
    }
}
