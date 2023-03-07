package com.weave.weaveserver.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.weave.weaveserver.config.exception.ConflictException;
import com.weave.weaveserver.config.exception.ForbiddenException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.exception.UnAuthorizedException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.*;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveService {

//    @Autowired
//    private ImageService imageService;
    private final FireBaseService fireBaseService;

    public final CategoryRepository categoryRepository;
    public final ArchiveRepository archiveRepository;
    public final ImageRepository imageRepository;

    // [외부사용] findByArchiveIdx
    public Archive findByArchiveIdx(Long archiveIdx) {
        log.info("[INFO] findByArchiveIdx : called");
        return archiveRepository.findByArchiveIdx(archiveIdx);
    }

    //[외부사용] deleteByTeamIdx
    public void deleteByTeamIdx(Team team) {
        log.info("[INFO] deleteByTeamIdx : called");
        List<Archive> archives = archiveRepository.findByTeam(team);
        if(archives.isEmpty()) {
            log.info("[INFO] deleteByTeamIdx : No archives to delete");
        }
        else {
            archiveRepository.deleteByTeam(team);
        }
    }

    //[외부사용] setUserNullByUser
    public void setUserNullByUser(User deleteUser) {
        log.info("[INFO] setUserNullByUser : called");
        List<Archive> archiveList = archiveRepository.findByUser(deleteUser);
        for(Archive a : archiveList){
            a.setUserNull();
        }
        archiveRepository.saveAll(archiveList);
    }


    public void addArchive(ArchiveRequest.createRequest request,
                           String fileName,
                           MultipartFile file,
                           Team team,
                           User clientUser) throws IOException, FirebaseAuthException {

        Category category = categoryRepository.findByCategoryIdx(request.getCategoryIdx());
        if(category == null){
            log.info("[REJECT] addArchive : category == null");
            throw new ConflictException("Category is not found by the categoryIdx in request body");
        }

        if(request.getTitle().isBlank()){ //""인지 + 공백으로만 된 문자열인지 검사
            log.info("[REJECT] addArchive : title is empty or blank");
            throw new ConflictException("Title of archive cannot be empty or blank");
        }

        Archive archive = Archive.builder()
                .user(clientUser)
                .team(team)
                .title(request.getTitle())
                .content(request.getContent())
                .url(request.getUrl())
                .imageUrl(null) //TODO : 추후 DTO구조 수정, 컬럼 제거
                .isPinned(false) //처음 생성 시 기본값
                .category(category)
                .build();
        archiveRepository.save(archive);

        //이미지 유무 확인 및 업로드하여 url 받아오기
        String imgUrl = "";
        if(fileName == null || file == null){
            log.info("[INFO] addArchive : no file to upload");
        } else {
            log.info("[INFO] addArchive : upload file");
            imgUrl = fireBaseService.uploadFiles("archive", fileName, file);  //이미지 업로드 후 url받아오기!!

            //이미지는 이미지 테이블에 따로 저장. 위에서 저장한 아카이브를 참조함.
            Image image = Image.builder()
                    .archive(archive)
                    .url(imgUrl)
                    .build();
            imageRepository.save(image);
        }

    }


    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    public ArchiveResponse.archiveListResponseContainer getArchiveList(Team team){

        List<LocalDate> dateList = team.getStartDate().datesUntil(team.getEndDate().plusDays(1))
                .collect(Collectors.toList());
        TeamResponse.teamWithDateListResponse teamResponse = new TeamResponse.teamWithDateListResponse(
                team.getTeamIdx(),
                team.getTitle(),
                team.getStartDate(),
                team.getEndDate(),
                dateList,
                team.getImgUrl()
        );


        //ArchiveList
        List<Archive> archiveList = archiveRepository.findByTeam(team);

        //ArchiveList를 돌면서 각 아카이브에 해당하는 이미지 하나씩 & 작성자user 가져오기
        Map<Long, ImageResponse.imageResponse> imageList = new HashMap();
        Map<Long, UserResponse.userResponse> userList = new HashMap();
        for(Archive a : archiveList) {
            Long archiveIdx = a.getArchiveIdx();

            //User
            Optional<User> archiveUserOp = Optional.ofNullable(a.getUser());
            if(archiveUserOp.isPresent()){
                User archiveUser = archiveUserOp.get();
                UserResponse.userResponse userResponse = new UserResponse.userResponse(
                        archiveUser.getName(),
                        archiveUser.getEmail()
                );
                userList.put(archiveIdx, userResponse);
            }else{
                UserResponse.userResponse userResponse = new UserResponse.userResponse("", "");
                userList.put(archiveIdx, userResponse);
            }

            //Image
            Image image = imageRepository.findTop1ByArchiveOrderByImageIdxAsc(a);
            if(image == null){
                imageList.put(archiveIdx, null);
            }
            else{
                ImageResponse.imageResponse imageResponse = new ImageResponse.imageResponse (
                        image.getImageIdx(),
                        image.getUrl(),
                        archiveIdx
                );
                imageList.put(archiveIdx, imageResponse);
            }
        }

        //archiveResponse 생성
        List<ArchiveResponse.archiveListResponse> archiveResponseList = archiveList.stream().map(archive ->
                new ArchiveResponse.archiveListResponse(
                archive.getArchiveIdx(),
                new CategoryResponse.categoryResponse(archive.getCategory().getCategoryIdx(), archive.getCategory().getCategoryName()),
                userList.get(archive.getArchiveIdx()),
                archive.getTitle(),
                archive.getContent(),
                imageList.get(archive.getArchiveIdx()), //이미지
                archive.isPinned())
        ).collect(Collectors.toList());

        //response 생성
        ArchiveResponse.archiveListResponseContainer response = new ArchiveResponse.archiveListResponseContainer(teamResponse, archiveResponseList);

        return response;
    }


//    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    public ArchiveResponse.archiveResponse getArchiveDetail(Archive archive){

        //User
        User archiveUser = archive.getUser();
        UserResponse.userResponse userResponse = new UserResponse.userResponse(
                archiveUser.getName(),
                archiveUser.getEmail()
        );

        //ImageList
        List<Image> imageList = imageRepository.findByArchiveIdx(archive.getArchiveIdx());
        List<ImageResponse.imageResponse> imageResponseList = new ArrayList();
        for(Image i : imageList){
            ImageResponse.imageResponse imageResponse = new ImageResponse.imageResponse(
                    i.getImageIdx(),
                    i.getUrl(),
                    i.getArchive().getArchiveIdx()
            );
            imageResponseList.add(imageResponse);
        }

        //response 생성
        ArchiveResponse.archiveResponse response = new ArchiveResponse.archiveResponse(
                        archive.getArchiveIdx(),
                        new CategoryResponse.categoryResponse(archive.getCategory().getCategoryIdx(), archive.getCategory().getCategoryName()),
                        userResponse,
                        archive.getTitle(),
                        archive.getContent(),
                        archive.getUrl(),
                        imageResponseList, //Image 리스트
                        archive.isPinned());

        return response;
    }

    public void updateArchivePin(Archive archive){
        archive.updateArchive(false);
        archiveRepository.save(archive);
    }

    @Transactional
    public void deleteArchive(Archive archive){
        //아카이브에 연결된 이미지 먼저 이미지서버에서 삭제
        List<Image> imageList = imageRepository.findByArchiveIdx(archive.getArchiveIdx());

        if(imageList != null && imageList.size() > 0){
            for(Image i : imageList){
                //파일 url에서 파일이름만 분리
                String url = i.getUrl();
                int startIndex = url.lastIndexOf("/") + 1; // 이 위치포함 시작
                int endIndex = url.lastIndexOf("?"); // 이 위치 직전까지만 포함
                String fileName = url.substring(startIndex, endIndex);

                //파일이름으로 파일 삭제
                fireBaseService.deleteFiles(fileName);
            }
        }

        //아카이브 삭제
        archiveRepository.delete(archive);
    }

    //TODO : 휘영이 이거 써서 아카이브들 삭제하면 됩니다~!! 감사합니당 :)
    @Transactional
    public void deleteAllArchiveByUserIdx(User user){
        archiveRepository.deleteAllByUserIdx(user.getUserIdx());
    }

}
