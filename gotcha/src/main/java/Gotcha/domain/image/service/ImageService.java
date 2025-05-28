package Gotcha.domain.image.service;

import Gotcha.domain.image.exception.ImageExceptionCode;
import gotcha_common.exception.CustomException;
import gotcha_common.s3.S3ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3ClientService s3ClientService;

    public String uploadImage(String userUuid, MultipartFile file) {
        String filename = generateUserFileName(userUuid, file);
        s3ClientService.uploadFile(filename, file);
        return filename;
    }

    private String generateUserFileName(String userUuid, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        if(extension.isEmpty() || (!extension.equals(".jpg") && !extension.equals(".png") && !extension.equals(".jpeg"))){
            throw new CustomException(ImageExceptionCode.INVALID_IMAGE_TYPE);
        }

        String uuid = UUID.randomUUID().toString();
        return userUuid + "/" + uuid + extension;
    }

}
