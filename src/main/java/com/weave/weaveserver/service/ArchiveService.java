package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.*;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArchiveService {
    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final CategoryRepository categoryRepository;
    public final ArchiveRepository archiveRepository;
    public final ImageRepository imageRepository;

    public void addArchive(ArchiveRequest.createRequest request){
        User user = userRepository.getReferenceById(request.getUserIdx());
        Team team = teamRepository.getReferenceById(request.getTeamIdx());
        Category category = categoryRepository.getReferenceById(request.getCategoryIdx());
        Archive archive = Archive.builder()
                .user(user)
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
    public List<ArchiveResponse.archiveListResponse> getArchiveList(Long teamIdx){
        Team team = teamRepository.findByTeamIdx(teamIdx);
        List<Archive> archiveList = archiveRepository.findByTeam(team);

        TeamResponse.teamResponse teamResponse = new TeamResponse.teamResponse(
                team.getTeamIdx(),
                team.getTitle(),
                team.getStartDate(),
                team.getEndDate(),
                team.getImgUrl()
        );

        //아카이브리스트를 돌면서 각 아카이브에 해당하는 이미지 한장씩 가져오기
        Map<Long, ImageResponse.imageResponse> imageList = new HashMap();
        for(Archive a : archiveList) {
            Image image = imageRepository.findTop1ByArchiveOrderByImageIdxAsc(a);
            if(image == null){
                imageList.put(a.getArchiveIdx(), null);
            }else{
                ImageResponse.imageResponse imageResponse = new ImageResponse.imageResponse (
                        image.getImageIdx(),
                        image.getUrl(),
                        image.getArchive().getArchiveIdx()
                );
                imageList.put(a.getArchiveIdx(), imageResponse);
            }
        }

        //response 생성
        List<ArchiveResponse.archiveListResponse> responseList = archiveList.stream().map(archive ->
                new ArchiveResponse.archiveListResponse(
                archive.getArchiveIdx(),
                new CategoryResponse.categoryResponse(archive.getCategory().getCategoryIdx(), archive.getCategory().getCategoryName()),
//                archive.getCategory().getCategoryIdx(),
//                archive.getCategory().getCategoryName(),
                teamResponse,
//                archive.getTeam().getTeamIdx(),
                archive.getUser().getUserIdx(),
                archive.getTitle(),
                archive.getContent(),
                imageList.get(archive.getArchiveIdx()), //이미지
                archive.isPinned())
        ).collect(Collectors.toList());

        return responseList;
    }


    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    public ArchiveResponse.archiveResponse getArchiveDetail(Long archiveIdx){
        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
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

        ArchiveResponse.archiveResponse response = new ArchiveResponse.archiveResponse(
                        archive.getArchiveIdx(),
                        new CategoryResponse.categoryResponse(archive.getCategory().getCategoryIdx(), archive.getCategory().getCategoryName()),
//                        archive.getCategory().getCategoryIdx(),
//                        archive.getCategory().getCategoryName(),
//                        archive.getTeam().getTeamIdx(),
                        archive.getUser().getUserIdx(),
                        archive.getTitle(),
                        archive.getContent(),
                        archive.getUrl(),
                        imageResponseList, //Image 리스트
                        archive.isPinned());

        return response;
    }

    public void updateArchivePin(Long archiveIdx){
        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
        archive.updateArchive(false);
        archiveRepository.save(archive);
    }

    @Transactional
    public void deleteArchive(Long archiveIdx){
        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
        imageRepository.deleteByArchive(archive);
        archiveRepository.deleteByArchiveIdx(archiveIdx);
    }

}
