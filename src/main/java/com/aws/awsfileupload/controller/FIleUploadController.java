package com.aws.awsfileupload.controller;

import com.aws.awsfileupload.service.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/content/assets")
public class FIleUploadController {

    @Autowired
    private FileUpload fileUpload;


    @PostMapping("/videos")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        Long trackerId=fileUpload.trackerIdGenerator();
        fileUpload.UploadAwsFile(trackerId,multipartFile);
        return "trackerId: "+trackerId;
    }

    @PostMapping("/cancel/{trackerId}")
    public String cancelUploading(@PathVariable("trackerId") Long trackerId) {
      return   fileUpload.tracker(trackerId);
    }


}
