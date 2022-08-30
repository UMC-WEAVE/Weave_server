package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.ArchiveResponse;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    @PostMapping("/archives")
    public ResponseEntity<Object> createArchive(@RequestPart ArchiveRequest.createRequest request,
                                                @RequestPart("fileName") @Nullable String fileName,
                                                @RequestPart("file") @Nullable MultipartFile file,
                                                HttpServletRequest servletRequest) throws IOException { //RequestBody 에 입력값들을 담고, Header 에 유저의 토큰을 담아 보냄.
        System.out.println("jh request : "+request);
        System.out.println("jh fileName : "+fileName);
        if(file != null){
            System.out.println("jh file : "+file);
            System.out.println("jh file.getContentType : "+file.getContentType());
            System.out.println("jh file.getSize : "+file.getSize());
        } else{
            System.out.println("jh file : file == null");
        }
        archiveService.addArchive(request, fileName, file, servletRequest);
        return ResponseEntity.ok(new JsonResponse(201, "Archive successfully created", null));
    }

    @GetMapping("/teams/{teamIdx}/archives")
    public ResponseEntity<Object> getArchiveList(@PathVariable Long teamIdx, HttpServletRequest servletRequest){
        ArchiveResponse.archiveListResponseContainer archiveListContainer = archiveService.getArchiveList(teamIdx, servletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveList", archiveListContainer));
    }

    @GetMapping("/archives/{archiveIdx}")
    public ResponseEntity<Object> getArchiveDetail(@PathVariable Long archiveIdx, HttpServletRequest servletRequest){
        ArchiveResponse.archiveResponse archiveDetail = archiveService.getArchiveDetail(archiveIdx, servletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveDetail", archiveDetail));
    }

    @PatchMapping("/archives/{archiveIdx}/pin")
    public ResponseEntity<Object> updateArchivePin(@PathVariable Long archiveIdx, HttpServletRequest servletRequest){
        archiveService.updateArchivePin(archiveIdx, servletRequest);
        return ResponseEntity.ok(new JsonResponse(200, "success updateArchivePin", null));
    }

    @DeleteMapping("/archives/{archiveIdx}")
    public ResponseEntity<Object> deleteArchive(@PathVariable Long archiveIdx, HttpServletRequest servletRequest){
        archiveService.deleteArchive(archiveIdx, servletRequest);
        return ResponseEntity.ok(new JsonResponse(204, "Archive successfully deleted", null));
    }

}
