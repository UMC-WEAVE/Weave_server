package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    @PostMapping("/archives")
    public ResponseEntity<?> createArchive(@RequestBody ArchiveRequest.createRequest request){
        archiveService.addArchive(request);
        return ResponseEntity.ok(new JsonResponse(200, "success addArchive", null));
    }
}
