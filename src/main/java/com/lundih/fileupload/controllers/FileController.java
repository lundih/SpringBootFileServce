package com.lundih.fileupload.controllers;

import com.lundih.fileupload.dto.FileUploadResponse;
import com.lundih.fileupload.services.FileEntityService;
import com.lundih.fileupload.services.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("api/v1/")
public class FileController {
    private final FileService fileService;
    private final FileEntityService fileEntityService;

    public FileController(FileService fileService, FileEntityService fileEntityService) {
        this.fileService = fileService;
        this.fileEntityService = fileEntityService;
    }

    @PostMapping("single/upload")
    public FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) {
        fileEntityService.saveFileData(file);

        return fileService.storeFile(file);
    }

    @PostMapping("multiple/upload")
    public List<FileUploadResponse> multipleFileUpload(@RequestParam("files") MultipartFile[] files) {
        fileEntityService.saveFilesData(files);

        return fileService.storeFiles(files);
    }

    @GetMapping("single/download/{fileName}")
    public ResponseEntity<Resource> singleFileDownload(@PathVariable String fileName, HttpServletRequest request) {
        return fileService.downloadFile(fileName, request);
    }

    @GetMapping("single/download-with-id/{id}")
    public ResponseEntity<Resource> singleFileDownloadWithId(@PathVariable Integer id, HttpServletRequest request) {
        String filename = fileEntityService.getFileNameFromId(id);

        return fileService.downloadFile(filename, request);
    }

    @GetMapping("single/render/{fileName}")
    public ResponseEntity<Resource> singleFileRender(@PathVariable String fileName, HttpServletRequest request) {
        return fileService.renderFile(fileName, request);
    }

    // http://localhost:[port]/multiple/zipdownload?filename=[filename]&filename=[filename2]
    @GetMapping("multiple/zipdownload")
    public void zipDownload(@RequestParam("filename") String[] fileNames, HttpServletResponse response) {
        try {
            fileService.downloadZip(fileNames, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("multiple/zipdownload-with-ids")
    public void zipDownloadWithIds(@RequestParam("id") Integer[] ids, HttpServletResponse response) {
        String[] filesNames = new String[ids.length];
        for (int i=0; i<ids.length; i++)
            filesNames[i] = fileEntityService.getFileNameFromId(ids[i]);

        zipDownload(filesNames, response);
    }
}
