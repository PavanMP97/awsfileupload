package com.aws.awsfileupload;

import com.amazonaws.services.s3.transfer.TransferManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableAsync
@SpringBootApplication
public class AwsfileuploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsfileuploadApplication.class, args);
	}

}
