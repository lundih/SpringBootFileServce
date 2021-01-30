package com.lundih.fileupload.controller;

import com.lundih.fileupload.dto.FileUploadResponse;
import com.lundih.fileupload.services.FileEntityService;
import com.lundih.fileupload.services.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Integration test annotation
@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    @DisplayName("Test for single file upload")
    void uploadSingleFileTest() throws Exception {
        // Response expected when multipart file is provided
        Mockito.when(fileService.storeFile(any(MockMultipartFile.class)))
                .thenReturn(new FileUploadResponse("TextFile.txt",
                        "text/plain",
                        "http://localhost:8080/api/v1/single/download/TextFile.txt"));
        // Create multipart file
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", // Should match the controller's @RequestParam
                "TestFile.txt",
                "text/plain",
                "Hello World!".getBytes());
        // Call endpoint with multipart file
        mockMvc.perform(multipart("http://localhost:8080/api/v1/single/upload")
                        .file(mockMultipartFile))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"name\":\"TextFile.txt\"," +
                            "\"contentType\":\"text/plain\"," +
                            "\"url\":\"http://localhost:8080/api/v1/single/download/TextFile.txt\"}"));
        // Verify that the service called the storeFile method (because we are actually receiving a mock response)
        BDDMockito.then(this.fileService).should().storeFile(mockMultipartFile);
    }

    @Test
    @DisplayName("Test for single file download")
    void downloadSingleFileTest() throws Exception {
        Resource resource = getResourceMock();
        ResponseEntity<Resource> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.TEXT_PLAIN.toString()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resource.getFilename())
                .body(resource);
        Mockito.when(fileService.downloadFile(anyString() ,any(HttpServletRequest.class)))
                .thenReturn(responseEntity);
        mockMvc.perform(get("http://localhost:8080/api/v1/single/download/Image.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + resource.getFilename()))
                .andExpect(content().bytes("Image content".getBytes()));
        BDDMockito.then(fileService).should().downloadFile(anyString(), any(HttpServletRequest.class));
    }

    private Resource getResourceMock() {
        return new Resource() {
            @Override
            public boolean exists() {
                return false;
            }

            @Override
            public URL getURL() throws IOException {
                return null;
            }

            @Override
            public URI getURI() throws IOException {
                return null;
            }

            @Override
            public File getFile() throws IOException {
                return null;
            }

            @Override
            public long contentLength() throws IOException {
                return 0;
            }

            @Override
            public long lastModified() throws IOException {
                return 0;
            }

            @Override
            public Resource createRelative(String relativePath) throws IOException {
                return null;
            }

            @Override
            public String getFilename() {
                return "Image.jpg";
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("Image content".getBytes());
            }
        };
    }
}
