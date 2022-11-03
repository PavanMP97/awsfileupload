package com.aws.awsfileupload.repository;

import com.aws.awsfileupload.domain.FileContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileContent, Long> {
}
