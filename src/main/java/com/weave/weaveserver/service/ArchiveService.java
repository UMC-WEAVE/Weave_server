package com.weave.weaveserver.service;

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

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;

//    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final BelongRepository belongRepository;
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


    public void addArchive(ArchiveRequest.createRequest request,
                           String fileName,
                           MultipartFile file,
                           HttpServletRequest servletRequest) throws IOException {
        User clientUser = findUserByEmailInToken(servletRequest);

        //Team team = teamRepository.getReferenceById(request.getTeamIdx()); //TODO : team없을 때 이 에러 잡는 법 모르겠음!! SQL단위 에러인 듯
        Team team = teamRepository.findByTeamIdx(request.getTeamIdx());
        if(team == null){
            log.info("[REJECT] addArchive : team == null");
            throw new ConflictException("Team is not found by the teamIdx in request body");
        }

        checkBelong(team.getTeamIdx(), clientUser.getEmail());

//        Category category = categoryRepository.getReferenceById(request.getCategoryIdx()); //TODO : 동일
        Category category = categoryRepository.findByCategoryIdx(request.getCategoryIdx());
        if(category == null){
            log.info("[REJECT] addArchive : category == null");
            throw new ConflictException("Category is not found by the categoryIdx in request body");
        }

        if(request.getTitle().isBlank()){ //""인지 + 공백으로만 된 문자열인지 검사
            log.info("[REJECT] addArchive : title is empty or blank");
            throw new ConflictException("Title of archive cannot be empty or blank");
        }

        //이미지 유무 확인 및 업로드하여 url 받아오기
        String imgUrl = "";
        if(fileName == null || file == null){
            log.info("[INFO] addArchive : no file to upload");
        } else {
            log.info("[INFO] addArchive : upload file");
            imgUrl = imageService.uploadToStorage("archive", fileName, file); //이미지 업로드 후 url받아오기!!
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

        //이미지는 이미지 테이블에 따로 저장. 위에서 저장한 아카이브를 참조함.
        Image image = Image.builder()
                .archive(archive)
                .url(imgUrl)
                .build();
        imageRepository.save(image);
    }

    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    public ArchiveResponse.archiveListResponseContainer getArchiveList(Long teamIdx, HttpServletRequest servletRequest){
        User clientUser = findUserByEmailInToken(servletRequest);

        //Team
        Team team = teamRepository.findByTeamIdx(teamIdx);
        if(team == null){
            log.info("[REJECT] getArchiveList : team == null");
            throw new NotFoundException("Team is not found by this teamIdx");
        }

        checkBelong(team.getTeamIdx(), clientUser.getEmail());

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
            User archiveUser = a.getUser();
            UserResponse.userResponse userResponse = new UserResponse.userResponse(
                    archiveUser.getName(),
                    archiveUser.getEmail()
            );
            userList.put(archiveIdx, userResponse);

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


    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    public ArchiveResponse.archiveResponse getArchiveDetail(Long archiveIdx, HttpServletRequest servletRequest){
        User clientUser = findUserByEmailInToken(servletRequest);

        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
//        Optional<Archive> archive = archiveRepository.findById(archiveIdx);
//        Archive archive = archiveRepository.findById(archiveIdx)
//                .orElseThrow(() -> new NotFoundException("아카이브가 존재하지 않습니다... 같은 에러를 던짐"));
        if(archive == null){
            log.info("[REJECT] getArchiveDetail : archive == null");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }

        Team team = archive.getTeam();
        checkBelong(team.getTeamIdx(), clientUser.getEmail());

        //User
        User archiveUser = archive.getUser();
        UserResponse.userResponse userResponse = new UserResponse.userResponse(
                archiveUser.getName(),
                archiveUser.getEmail()
        );

        //ImageList
        List<Image> imageList = imageRepository.findByArchiveIdx(archiveIdx);
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

    public void updateArchivePin(Long archiveIdx, HttpServletRequest servletRequest){
        User clientUser = findUserByEmailInToken(servletRequest);

        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
        if(archive == null){
            log.info("[REJECT] updateArchivePin : archive == null");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }

        Team team = archive.getTeam();
        checkBelong(team.getTeamIdx(), clientUser.getEmail());

        archive.updateArchive(false);
        archiveRepository.save(archive);
    }

    @Transactional
    public void deleteArchive(Long archiveIdx, HttpServletRequest servletRequest){
        User clientUser = findUserByEmailInToken(servletRequest);

        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
        if(archive != null) { //archiveIdx에 해당하는 archive가 존재할 때만 belong체크와 삭제 실행
            Team team = archive.getTeam();
            checkBelong(team.getTeamIdx(), clientUser.getEmail());

//        imageRepository.deleteByArchive(archive);
            archiveRepository.deleteByArchiveIdx(archiveIdx);
        }
        else {
            log.info("[REJECT] deleteArchive : archive == null. Delete nothing");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }

    }


    //TODO : 휘영이 이거 써서 아카이브들 삭제하면 됩니다~!!
    @Transactional
    public void deleteAllArchiveByUserIdx(HttpServletRequest servletRequest){
        User user = findUserByEmailInToken(servletRequest);

        archiveRepository.deleteAllByUserIdx(user.getUserIdx());
    }


    private User findUserByEmailInToken(HttpServletRequest servletRequest){
//        System.out.println(servletRequest);
        if(servletRequest == null){
            log.info("[REJECT] archive findUserByEmailInToken : servletRequest == null");
            throw new UnAuthorizedException("Unauthorized. HttpServletRequest is null");
        }
        String userEmail = tokenService.getUserEmail(servletRequest); // 토큰으로부터 user 이메일 가져오기
        User clientUser = userService.getUserByEmail(userEmail);

        return clientUser;
    }

    private void checkBelong(Long teamIdx, String email){
        Belong belong = belongRepository.findByTeamIdxAndUser(teamIdx, email);
        if(belong == null){ //TODO : belong 사용방식 변경으로 수정 예정
            log.info("[REJECT] archive checkBelong : belong == null");
            throw new ForbiddenException("Forbidden. User is not belong in the team");
        }
    }

}
