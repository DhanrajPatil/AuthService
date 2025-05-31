package com.elitefolk.authservice.aws;

import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
/**
 * Service class for interacting with AWS S3.
 * This class provides methods to upload and delete files in an S3 bucket.
 */
public class AwsSimpleStorageService {

    private S3Client s3Client;
    private String region;

//    @Value("${aws.s3.bucket-name}")
//    private String bucketName;

    /**
     * Constructor for AwsSimpleStorageService.
     *
     * @param region      AWS region for S3
     * @param s3AccessKey AWS S3 access key
     * @param s3SecretKey AWS S3 secret key
     */
    public AwsSimpleStorageService(@Value("${aws.region}") String region,
                                   @Value("${aws.s3.access-key}") String s3AccessKey,
                                   @Value("${aws.s3.secret-key}") String s3SecretKey) {
        this.region = region;
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> AwsBasicCredentials.create(s3AccessKey, s3SecretKey))
                .build();
    }

    /**
     * Uploads a file to the specified S3 bucket.
     *
     * @param file The file to be uploaded
     * @return The URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, String bucketName) {
        String fileName = UuidCreator.getTimeOrdered() + "_" + file.getOriginalFilename();
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    /**
     * Deletes a file from the specified S3 bucket.
     *
     * @param fileName The name of the file to be deleted
     * @return A message indicating success or failure
     */
    public String deleteFile(String fileName, String bucketName) {
        try {
            s3Client.deleteObject(
                    builder -> builder.bucket(bucketName).key(fileName)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Deleted";
    }

}
