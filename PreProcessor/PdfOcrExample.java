package com.example.demo;
import com.example.demo.OCRPreProcessor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class PdfOcrExample {
    private final OCRPreProcessor preProcessor;
    private final BankingDictionaryCorrector corrector;

    public PdfOcrExample(OCRPreProcessor preProcessor, BankingDictionaryCorrector corrector){
        this.preProcessor = preProcessor;
        this.corrector = corrector;
    }

    public String extractText(File pdfFile, String bank) throws Exception{
        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            ITesseract tesseract = new Tesseract();

            // Set path to tessdata (important!)
            tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata/");
            tesseract.setLanguage("ron");

            StringBuilder result = new StringBuilder();

            for (int page = 0; page < document.getNumberOfPages(); page++) {

                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300);
                File tempFile = File.createTempFile("page_" + page, ".png");
                javax.imageio.ImageIO.write(image, "png", tempFile);
                BufferedImage processed = preProcessor.processImage(tempFile, bank);
                String pageText = tesseract.doOCR(processed);
                String correctedText = corrector.correctText(pageText);
                result.append(pageText).append("\n");
                tempFile.delete();
                System.out.println("Processed page: " + page);
            }
            return result.toString();
        }
    }
}
