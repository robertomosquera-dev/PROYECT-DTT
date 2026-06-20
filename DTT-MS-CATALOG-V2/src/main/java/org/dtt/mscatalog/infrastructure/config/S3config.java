package org.dtt.mscatalog.infrastructure.config;


import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.infrastructure.utils.PropertiesConfigurationS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Profile("docker")
@Configuration
@RequiredArgsConstructor
public class S3config {

    private final PropertiesConfigurationS3 propertiesConfigurationS3;

    @Bean
    public S3Client getS3Client(){

        AwsCredentials awsCredentials = AwsBasicCredentials
                .create(
                        propertiesConfigurationS3.accessKey(),
                        propertiesConfigurationS3.secretKey()
                );

        return  S3Client
                .builder()
                .region(Region.of(propertiesConfigurationS3.region()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

}
