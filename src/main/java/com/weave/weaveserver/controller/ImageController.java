package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    @Autowired
    private ImageService imageService;


    @PostMapping(value = "/image/upload")
    public Object uploadToStorage(@RequestParam String fileName, MultipartFile file)throws IOException{
        String fileRes = imageService.uploadToStorage("test",fileName,file);
        return ResponseEntity.ok(new JsonResponse(200,"uploadToStorage",fileRes));
    }

}
