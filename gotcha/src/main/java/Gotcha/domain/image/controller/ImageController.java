package Gotcha.domain.image.controller;

import Gotcha.domain.image.api.ImageApi;
import Gotcha.domain.image.service.ImageService;
import gotcha_common.dto.SuccessRes;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController implements ImageApi {

    private final ImageService imageService;


    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, SecurityUserDetails userDetails) {
        String filename = imageService.uploadImage(userDetails.getUuid(), file);
        return ResponseEntity.ok(SuccessRes.from(filename));
    }

}
