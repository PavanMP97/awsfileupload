package com.aws.awsfileupload.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.aws.awsfileupload.domain.FileContent;
import com.aws.awsfileupload.domain.Status;
import com.aws.awsfileupload.domain.Tracker;
import com.aws.awsfileupload.repository.FileRepository;
import com.aws.awsfileupload.repository.TrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.concurrent.CancellationException;


@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService extends Thread implements FileUpload {

    private final AmazonS3Client amazonS3Client;

    private final FileRepository fileRepository;

    private final TrackerRepository trackerRepository;

    private PutObjectRequest putObjectRequest;

    private Upload upload;


    @Async
    public Void UploadAwsFile(Long trackerId, MultipartFile multipartFile) {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String key = UUID.randomUUID().toString() + "." + extension;
        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);
        File file = convertMultiPartFileToFile(multipartFile);
        putObjectRequest = new PutObjectRequest("uploadfilesprings", key, file);
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonS3Client).build();
        upload(trackerId, key, tm, putObjectRequest);
        return null;
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (Exception e) {
            System.out.println("Error converting multipartFile to file" + e);
        }
        return convertedFile;
    }

    private String upload(Long trackerId, String key, TransferManager tm, PutObjectRequest putObjectRequest) {
        putObjectRequest.setGeneralProgressListener(event -> {
            int percentage = (int) upload.getProgress().getPercentTransferred();
            System.out.println(percentage);
        });
        upload = tm.upload(putObjectRequest);
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            System.out.println("File uploading has been canceled..............");
            trackerRepository.updateStatusById(Status.UPLOADING_FAILED, trackerId);

        }
        String url = uploadURLToRepository(key);
        FileContent fileContent = new FileContent(url);
        fileRepository.save(fileContent);
        trackerRepository.updateStatusById(Status.UPLOADED, trackerId);
        log.info("File uploaded successfully......");
        return url;
    }

    public String tracker(Long trackerId) {
        if (trackerRepository.getReferenceById(trackerId).getStatus() != Status.UPLOADED && trackerRepository.getReferenceById(trackerId).getStatus() != Status.UPLOADING_CANCELED) {
            try {
                upload.abort();
                trackerRepository.updateStatusById(Status.UPLOADING_CANCELED, trackerId);
                return "Upload canceled..........";
            } catch (CancellationException e) {
                return "can't able to cancel the upload...... ";
            }
        } else if (trackerRepository.getReferenceById(trackerId).getStatus() == Status.UPLOADING_FAILED) {
            return "file uploading failed......";
        } else if (trackerRepository.getReferenceById(trackerId).getStatus() == Status.UPLOADED) {
            return "file already uploaded.........";
        }
        return "file uploading cancelled already........";

    }

    public Long trackerIdGenerator() {
        Tracker tracker = new Tracker();
        Long trackerID = trackerRepository.save(tracker).getTrackerId();
        return trackerID;
    }

    private String uploadURLToRepository(String key) {
        amazonS3Client.setObjectAcl("uploadfilesprings", key, CannedAccessControlList.PublicRead);
        String url = amazonS3Client.getResourceUrl("uploadfilesprings", key);
        FileContent fileContent = new FileContent();
        fileContent.setUrl(url);
        fileRepository.save(fileContent);
        return url;
    }

    private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }
}


//    1. upload file from postMAN RETURN SOME IDENTIFIER ASYNC
//    2. get progress by identifier IDENTIFIER
//    3. abort/ cancel upload , pass i/p identider
