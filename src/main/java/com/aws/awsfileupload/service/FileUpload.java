package com.aws.awsfileupload.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    Void UploadAwsFile(Long trackerId, MultipartFile multipartFile);

    Long trackerIdGenerator();

    String tracker(Long trackerID);
}
