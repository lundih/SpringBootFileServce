package com.lundih.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileUploadApplication {
    public static String SINGLE_DOWNLOAD_URL = "api/v1/single/download/";

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
    }

}
