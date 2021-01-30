package com.lundih.fileupload.services;

import com.lundih.fileupload.FileUploadApplication;
import com.lundih.fileupload.entities.FileEntity;
import com.lundih.fileupload.repositories.FileEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileEntityService {
    private final FileEntityRepository fileEntityRepository;

    public FileEntityService(FileEntityRepository fileEntityRepository) {
        this.fileEntityRepository = fileEntityRepository;
    }

    public List<FileEntity> getAllFiles() {
        return fileEntityRepository.findAll();
    }

    public Optional<FileEntity> getFile(Integer id) {
        return fileEntityRepository.findById(id);
    }

    public void saveFileData(MultipartFile file) {
        String fileUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath() // Get current url path (///http://localhost:[port]
                .path(FileUploadApplication.SINGLE_DOWNLOAD_URL) // Append this path to the current url
                .path(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .toUriString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        fileEntityRepository.save(new FileEntity(fileUrl, timestamp));
    }

    public void saveFilesData(MultipartFile[] files) {
        for (MultipartFile file: files) {
           saveFileData(file);
        }
    }

    public String getFileNameFromId(Integer id) {
        return getFileNameFromUrl(getFileUrlFromId(id));
    }

    private String getFileUrlFromId(Integer id) {
        Optional<FileEntity> optionalFileEntity = fileEntityRepository.findById(id);
        if (optionalFileEntity.isPresent())
            return optionalFileEntity.get().getFileUrl();
        else throw new RuntimeException("The specified ID does not exist");
    }

    private String getFileNameFromUrl(String url) {
        return url.split(FileUploadApplication.SINGLE_DOWNLOAD_URL)[1];
    }
}
