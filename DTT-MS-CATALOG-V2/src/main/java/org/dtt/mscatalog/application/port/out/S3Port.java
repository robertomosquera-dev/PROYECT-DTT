package org.dtt.mscatalog.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface S3Port {
    String uploadFile(MultipartFile file);
    String getUrl(String key);
    void deleteFile(String key);
}
