package com.weave.weaveserver.controller;

import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.ArchiveResponse;
import com.weave.weaveserver.dto.JsonResponse;
import com.weave.weaveserver.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArchiveController {
    private final ArchiveService archiveService;

    @GetMapping("/log/archives")
    public ResponseEntity<Object> testLogger() {
        log.error("test error");
        log.warn("test warn");
        log.info("test info");
        log.debug("test debug"); //이 레벨 부터는 출력 안되게 설정되어있음
        log.trace("test trace");
        return ResponseEntity.ok(new JsonResponse(200, "Test log success", null));
    }

    @PostMapping("/archives")
    public ResponseEntity<Object> createArchive(@RequestPart ArchiveRequest.createRequest request,
                                                @RequestPart("fileName") @Nullable String fileName,
                                                @RequestPart("file") @Nullable MultipartFile file,
                                                HttpServletRequest servletRequest) throws IOException { //RequestBody 에 입력값들을 담고, Header 에 유저의 토큰을 담아 보냄.
        log.info("createArchive : android MultipartFile test log");
        //TODO : 어디까지 로그파일에 남겨도 되는 거지?? 아래 안드 테스트 코드는 굉장히 지저분한(자세한) 편인데 이런 것도 남기면 민폐인가?
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
