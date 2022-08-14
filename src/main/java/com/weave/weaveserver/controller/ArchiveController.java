package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.ArchiveResponse;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    @PostMapping("/archives")
    public ResponseEntity<Object> createArchive(@RequestBody ArchiveRequest.createRequest request, HttpServletRequest servletRequest){ //RequestBody 에 입력값들을 담고, Header 에 유저의 토큰을 담아 보냄.
        archiveService.addArchive(request, servletRequest);
        return ResponseEntity.ok(new JsonResponse(201, "Archive successfully created", null));
    }

    @GetMapping("/teams/{teamIdx}/archives")
    public ResponseEntity<Object> getArchiveList(@PathVariable Long teamIdx){
        ArchiveResponse.archiveListResponseContainer archiveListContainer = archiveService.getArchiveList(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveList", archiveListContainer));
    }

    @GetMapping("/archives/{archiveIdx}")
    public ResponseEntity<Object> getArchiveDetail(@PathVariable Long archiveIdx){
        ArchiveResponse.archiveResponse archiveDetail = archiveService.getArchiveDetail(archiveIdx);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveDetail", archiveDetail));
    }

    @PatchMapping("/archives/{archiveIdx}/pin")
    public ResponseEntity<Object> updateArchivePin(@PathVariable Long archiveIdx){
        archiveService.updateArchivePin(archiveIdx);
        return ResponseEntity.ok(new JsonResponse(200, "success updateArchivePin", null));
    }

    @DeleteMapping("/archives/{archiveIdx}")
    public ResponseEntity<Object> deleteArchive(@PathVariable Long archiveIdx){
        archiveService.deleteArchive(archiveIdx);
        return ResponseEntity.ok(new JsonResponse(204, "Archive successfully deleted", null));
    }

}
