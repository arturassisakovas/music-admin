package com.mAdmin.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;


@Service
public class AmazonClient {

    
    private AmazonS3 s3client;

    
    @Value("${amazon.region}")
    private String region;

    
    @Value("${amazon.bucketName}")
    private String bucketName;

    
    @Value("${amazon.accesskey}")
    private String accessKey;

    
    @Value("${amazon.secretKey}")
    private String secretKey;

    
    @PostConstruct
    private void init() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.setS3client(AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region).build());
    }

    
    public void uploadFile(File uploadedFile, String fileName) throws IOException {
        try {
            uploadFileTos3bucket(fileName, uploadedFile);
            uploadedFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void uploadFile(UploadedFile uploadedFile, String fileName) throws IOException {
        try {
            File file = convertUploadedFileToFile(uploadedFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public InputStream downloadFile(String path) {
        S3Object s3object = s3client.getObject(bucketName, path);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        return inputStream;
    }


    
    private File convertUploadedFileToFile(UploadedFile uploadedFile) throws IOException {
        File convFile = new File(uploadedFile.getFileName());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(uploadedFile.getContents());
        fos.close();
        return convFile;
    }

    
    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    
    public void deletFile(String path) {
        s3client.deleteObject(bucketName, path);
    }

    
    public AmazonS3 getS3client() {
        return s3client;
    }

    
    public void setS3client(AmazonS3 s3client) {
        this.s3client = s3client;
    }

}
