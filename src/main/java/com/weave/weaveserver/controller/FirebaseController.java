package com.weave.weaveserver.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.weave.weaveserver.service.FireBaseService;
import com.weave.weaveserver.service.testImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FirebaseController {
    private final testImage fireBaseService;

    @PostMapping("/test/firebase")
    public String uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("name") String name) {
        if(file.isEmpty()){
            return "is empty";
        }
        String img_url =null;
        try{
            img_url = fireBaseService.uploadFiles("archive", name, file);
        }catch (IOException | FirebaseAuthException e){
            log.info(e.getMessage());
        }
        return  img_url;
    }

    @DeleteMapping("/test/firebase")
    public void deleteFile(@RequestParam String name){
        fireBaseService.deleteFiles(name);
    }
}
