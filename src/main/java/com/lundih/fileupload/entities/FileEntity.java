package com.lundih.fileupload.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class FileEntity {
    @Id
    @SequenceGenerator(name="file_sequence", sequenceName = "file_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_sequence")
    private Integer id;
    private String fileUrl;
    private Timestamp timestamp;

    public FileEntity(Integer id, String fileUrl, Timestamp timestamp) {
        this.id = id;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }

    public FileEntity(String fileUrl, Timestamp timestamp) {
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }

    public FileEntity() { }

    public Integer getId() {
        return id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "File{" +
                "\nid = " + id + "," +
                "\npath = \"" + fileUrl + "\"," +
                "\ntimestamp = \"" + timestamp + "\"}";
    }
}
