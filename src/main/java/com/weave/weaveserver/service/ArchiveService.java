package com.weave.weaveserver.service;

import com.weave.weaveserver.config.exception.ConflictException;
import com.weave.weaveserver.config.exception.ForbiddenException;
import com.weave.weaveserver.config.exception.NotFoundException;
import com.weave.weaveserver.config.jwt.TokenService;
import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.*;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    @Autowired
    private TokenService tokenService;

    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final BelongRepository belongRepository;
    public final CategoryRepository categoryRepository;
    public final ArchiveRepository archiveRepository;
    public final ImageRepository imageRepository;


    public void addArchive(ArchiveRequest.createRequest request, HttpServletRequest servletRequest){
        User clientUser = findUserByEmailInToken(servletRequest);

        //Team team = teamRepository.getReferenceById(request.getTeamIdx()); //TODO : team없을 때 이 에러 잡는 법 모르겠음!! SQL단위 에러인 듯
        Team team = teamRepository.findByTeamIdx(request.getTeamIdx());
        if(team == null){
            System.out.println("jh : team == null");
            throw new ConflictException("Team is not found by the teamIdx in request body");
        }

        checkBelong(team.getTeamIdx(), clientUser.getEmail());

//        Category category = categoryRepository.getReferenceById(request.getCategoryIdx()); //TODO : 동일
        Category category = categoryRepository.findByCategoryIdx(request.getCategoryIdx());
        if(category == null){
            System.out.println("jh : category == null");
            throw new ConflictException("Category is not found by the categoryIdx in request body");
        }

        if(request.getTitle().isBlank()){ //""인지 + 공백으로만 된 문자열인지 검사
            System.out.println("jh : title is empty or blank");
            throw new ConflictException("Title of archive cannot be empty or blank");
        }

        Archive archive = Archive.builder()
                .user(clientUser)
                .team(team)
                .title(request.getTitle())
                .content(request.getContent())
                .url(request.getUrl())
                .imageUrl(request.getImageUrl())
                .isPinned(false) //처음 생성 시 기본값
                .category(category)
                .build();
        archiveRepository.save(archive);
    }

    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    public ArchiveResponse.archiveListResponseContainer getArchiveList(Long teamIdx, HttpServletRequest servletRequest){
        User clientUser = findUserByEmailInToken(servletRequest);

        //Team
        Team team = teamRepository.findByTeamIdx(teamIdx);
        if(team == null){
            System.out.println("jh : team == null");
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
        if(archive == null){
            System.out.println("jh : archive == null");
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
            System.out.println("jh : archive == null");
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
//            System.out.println("jh : archive == null. No delete");
            throw new NotFoundException("Archive is not found by this archiveIdx");
        }

    }

//    private void throwNotFoundException(String )
    private User findUserByEmailInToken(HttpServletRequest servletRequest){
        System.out.println(servletRequest);
        if(servletRequest == null){
            System.out.println("jh : servletRequest == null");
        }
        String userEmail = tokenService.getUserEmail(servletRequest); // 토큰으로부터 user 이메일 가져오기
        User clientUser = userRepository.findUserByEmail(userEmail);

        return clientUser;
    }

    private void checkBelong(Long teamIdx, String email){
        Belong belong = belongRepository.findByTeamIdxAndUser(teamIdx, email);
        if(belong == null){
            System.out.println("jh : belong == null");
            throw new ForbiddenException("Forbidden. User is not belong in the team");
        }
    }

}
