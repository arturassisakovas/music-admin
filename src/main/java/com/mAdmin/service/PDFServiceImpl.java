package com.mAdmin.service;

import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class PDFServiceImpl implements PDFService {

    
    @Override
    public File generateFile(String fileName, String html) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();

        File generatedFile = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(generatedFile);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.close();
        return generatedFile;
    }
}
