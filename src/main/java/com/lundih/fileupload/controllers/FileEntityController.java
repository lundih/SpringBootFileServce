package com.lundih.fileupload.controllers;

import com.lundih.fileupload.entities.FileEntity;
import com.lundih.fileupload.services.FileEntityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/file-data/")
public class FileEntityController {
    private final FileEntityService fileEntityService;

    public FileEntityController(FileEntityService fileEntityService) {
        this.fileEntityService = fileEntityService;
    }

    @GetMapping("all-files")
    public List<FileEntity> getAllFiles() {
        return fileEntityService.getAllFiles();
    }

    @GetMapping("file/{id}")
    public Optional<FileEntity> getFile(@PathVariable("id") Integer id) {
        return fileEntityService.getFile(id);
    }
}
