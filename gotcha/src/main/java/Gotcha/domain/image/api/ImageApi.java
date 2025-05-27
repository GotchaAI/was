package Gotcha.domain.image.api;


import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ImageApi {
    @PostMapping("/upload")
    ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, SecurityUserDetails userDetails);
}
