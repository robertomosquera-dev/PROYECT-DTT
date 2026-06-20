package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.S3Port;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class S3AdapterMockito implements S3Port {

    @Override
    public String uploadFile(MultipartFile file) {
        return "products/" + java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

    @Override
    public String getUrl(String key) {
        return "https://mock-s3.local/" + key;
    }

    @Override
    public void deleteFile(String key) {
        System.out.println("DEBUG: Mock S3 - Simulando eliminación del archivo con key: " + key);
    }


}
