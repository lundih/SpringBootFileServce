package com.lundih.fileupload.repositories;

import com.lundih.fileupload.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Integer> {

}
