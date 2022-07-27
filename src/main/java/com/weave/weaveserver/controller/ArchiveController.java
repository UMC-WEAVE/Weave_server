package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.ArchiveResponse;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    @PostMapping("/archives")
    public ResponseEntity<?> createArchive(@RequestBody ArchiveRequest.createRequest request){
        archiveService.addArchive(request);
        return ResponseEntity.ok(new JsonResponse(200, "success addArchive", null));
    }

    @GetMapping("/teams/{teamIdx}/archives")
    public ResponseEntity<?> getArchiveList(@PathVariable Long teamIdx){
        List<ArchiveResponse.archiveListResponse> archiveList = archiveService.getArchiveList(teamIdx);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveList", archiveList));
    }

    @GetMapping("/archives/{archiveIdx}")
    public ResponseEntity<?> getArchiveDetail(@PathVariable Long archiveIdx){
        ArchiveResponse.archiveResponse archiveDetail = archiveService.getArchiveDetail(archiveIdx);
        return ResponseEntity.ok(new JsonResponse(200, "success getArchiveDetail", archiveDetail));
    }

    @DeleteMapping("/archives/{archiveIdx}")
    public ResponseEntity<?> deleteArchive(@PathVariable Long archiveIdx){
        archiveService.deleteArchive(archiveIdx);
        return ResponseEntity.ok(new JsonResponse(200, "success deleteArchive", null));
    }

}
