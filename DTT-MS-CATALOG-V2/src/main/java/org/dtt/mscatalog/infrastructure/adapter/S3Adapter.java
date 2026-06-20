package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.S3Port;
import org.dtt.mscatalog.infrastructure.utils.PropertiesConfigurationS3;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Profile("docker")
@Service
@RequiredArgsConstructor
public class S3Adapter implements S3Port {

    private final S3Client s3Client;
    private final PropertiesConfigurationS3 props;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String key = "products/" +
                    UUID.randomUUID() +
                    "-" +
                    file.getOriginalFilename();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(props.bucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            return key;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public String getUrl(String key) {
        return "https://" +
                props.bucket() +
                ".s3." +
                props.region() +
                ".amazonaws.com/" +
                key;
    }

    @Override
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(props.bucket())
                            .key(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + key, e);
        }
    }
}