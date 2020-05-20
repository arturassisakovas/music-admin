package com.mAdmin.controller;

import java.io.File;
import java.io.InputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.mAdmin.service.AmazonClient;


@Controller
public class FileController {

    
    @Autowired
    private AmazonClient amazonClient;

    
    private DefaultStreamedContent download;

    
    private StreamedContent preview;

    
    private String fileName;

    
    public void download(String path) {

        try {
            File file = new File(path);
            InputStream input = amazonClient.downloadFile(path);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            setDownload(new DefaultStreamedContent(input, externalContext.getMimeType(file.getName()), file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void setUpPreview(String path) {

        try {
            InputStream stream = amazonClient.downloadFile(path);
            setPreview(new DefaultStreamedContent(stream, "application/pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void setDownload(DefaultStreamedContent download) {
        this.download = download;
    }

    
    public DefaultStreamedContent getDownload() {
        return download;
    }

    
    public StreamedContent getPreview() {
        return preview;
    }

    
    public void setPreview(StreamedContent preview) {
        this.preview = preview;
    }

    
    public String generateRandomIdForNotCaching() {
        return java.util.UUID.randomUUID().toString();
    }

    
    public void setFileName(String name) {
        this.fileName = name;
    }

    
    public String getFileName() {
        return fileName;
    }
}
