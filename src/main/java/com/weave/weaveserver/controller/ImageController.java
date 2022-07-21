package com.weave.weaveserver.controller;

import com.weave.weaveserver.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class ImageController {

    private S3Service s3Service;


    @PostMapping("/image")
    public String insertImg(MultipartFile file) throws IOException {
        String imgPath = s3Service.upload(file);
        return imgPath;
    }
}
