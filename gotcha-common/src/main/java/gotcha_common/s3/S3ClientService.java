

package gotcha_common.s3;


import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
@RequiredArgsConstructor
public class S3ClientService {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    // MultipartFile 이미지를 그대로 S3에 업로드
    public void uploadImage(String filename, MultipartFile file){
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch(Exception e){
            throw new CustomException(GlobalExceptionCode.INVALID_IMAGE);
        }
    }

}
