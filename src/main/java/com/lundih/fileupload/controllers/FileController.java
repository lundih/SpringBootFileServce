package com.lundih.fileupload.controllers;

import com.lundih.fileupload.dto.FileUploadResponse;
import com.lundih.fileupload.services.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("api/v1/")
public class FileController {

    public final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("single/upload")
    public FileUploadResponse singleFileUpload(@RequestParam("file")MultipartFile file) {
      return fileService.storeFile(file);
    }

    @PostMapping("multiple/upload")
    public List<FileUploadResponse> multipleFileUpload(@RequestParam("files") MultipartFile[] files) {
        return fileService.storeFiles(files);
    }

    @GetMapping("single/download/{fileName}")
    public ResponseEntity<Resource> singleFileDownload(@PathVariable String fileName, HttpServletRequest request) {
        return fileService.downloadFile(fileName, request);
    }

    @GetMapping("single/render/{fileName}")
    public ResponseEntity<Resource> singleFileRender(@PathVariable String fileName, HttpServletRequest request) {
        return fileService.renderFile(fileName, request);
    }

    @GetMapping("multiple/zipdownload")
    public void zipDownload(@RequestParam("fileName") String[] fileNames, HttpServletResponse response) {
        try {
            fileService.downloadZip(fileNames, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
