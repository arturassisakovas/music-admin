package com.mAdmin.service;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;


public interface PDFService {

    
    File generateFile(String fileName, String html) throws IOException, DocumentException;

}
