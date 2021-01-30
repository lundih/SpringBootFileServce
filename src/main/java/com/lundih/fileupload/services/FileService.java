package com.lundih.fileupload.services;

import com.lundih.fileupload.FileUploadApplication;
import com.lundih.fileupload.dto.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {
    private final Path fileStoragePath;
    private final String fileStorageLocation;

    // file.storage.location is gotten from the application.yaml file and temp is the default folder in case the
    // location is not found
    // We can also throw an exception instead of providing a default location
    public FileService(@Value("${file.storage.location:temp}") String fileStorageLocation) {
        // Get the storage path whenever the service is created
        fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        this.fileStorageLocation = fileStorageLocation;
        try {
            Files.createDirectories(fileStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Error encountered while creating the storage directory");
        }
    }

    public FileUploadResponse storeFile(MultipartFile file){
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Paths.get(fileStoragePath + "/" + fileName);
        try {
            // Copy contents of input stream into the file path with the selected strategy
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error encountered while saving the file");
        }
        String contentType = file.getContentType();
        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath() // Get current url path (///http://localhost:[port]
                .path(FileUploadApplication.SINGLE_DOWNLOAD_URL) // Append this path to the current url
                .path(fileName)
                .toUriString();

        return new FileUploadResponse(fileName, contentType, url);
    }

    public List<FileUploadResponse> storeFiles(MultipartFile[] files) {
        if (files.length > 5) throw new RuntimeException("Upload limit is 5 files");

        List<FileUploadResponse> fileUploadResponses = new ArrayList<>();
        Arrays.asList(files).forEach(file -> {
            fileUploadResponses.add(storeFile(file));
        });

        return fileUploadResponses;
    }

    public ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request) {
        Resource resource = getResource(fileName);
        String mimeType = getResourceMimeType(resource, request);

        return createResponseEntity(resource, mimeType, "attachment");
    }

    public ResponseEntity<Resource> renderFile(String fileName, HttpServletRequest request) {
        Resource resource = getResource(fileName);
        String mimeType = getResourceMimeType(resource, request);

        return createResponseEntity(resource, mimeType, "inline");
    }

    public void downloadZip(String[] fileNames, HttpServletResponse response) throws IOException {
        // Try with resources. Declares one or more resources that must be closed after the program is finished with it
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            Arrays.asList(fileNames).forEach(fileName -> {
                Resource resource = getResource(fileName);
                ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(resource.getFilename()));
                try {
                    zipEntry.setSize(resource.contentLength());
                    zipOutputStream.putNextEntry(zipEntry);
                    StreamUtils.copy(resource.getInputStream(), zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            zipOutputStream.finish();
        }
        response.setStatus(200);
    }

    private Resource getResource(String fileName) {
        Resource resource;
        Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error encountered while obtaining file path");
        }

        return resource;
    }

    private String getResourceMimeType(Resource resource, HttpServletRequest request) {
        if (!resource.exists() || !resource.isReadable())
            throw new RuntimeException("The file does not exist or could not be read");
        String mimeType;
        try {
            mimeType =  request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            // Assign default mime type in case an exception is encountered while trying to determine file mime type
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return mimeType;
    }

    private ResponseEntity<Resource> createResponseEntity(Resource resource, String mimeType, String action) {
        // "action" lets us choose what happens when we hit the endpoint
        // attachment lets us download the file
        // inline lets us render the file in the browser
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, action + ";filename=" + resource.getFilename())
                .body(resource);
    }
}
