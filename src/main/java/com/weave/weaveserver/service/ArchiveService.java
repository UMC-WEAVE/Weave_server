package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.*;
import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.dto.ArchiveResponse;
import com.weave.weaveserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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
        archiveRepository.save(archive); //insert
    }

    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    //TODO : image를 리스트로 바꾸기
    public List<ArchiveResponse.archiveListResponse> getArchiveList(Long teamIdx){
        List<Archive> archiveList = archiveRepository.findByTeamIdx(teamIdx);
        List<ArchiveResponse.archiveListResponse> responseList = archiveList.stream().map(archive ->
                new ArchiveResponse.archiveListResponse(
                archive.getArchiveIdx(),
                archive.getCategory().getCategoryIdx(),
                archive.getCategory().getCategoryName(),
                archive.getTeam().getTeamIdx(),
                archive.getUser().getUserIdx(),
                archive.getTitle(),
                archive.getContent(),
                archive.getImageUrl(),
                archive.isPinned())
        ).collect(Collectors.toList());
        return responseList;
    }


    @Transactional // 왜 이걸 붙이면 LAZY 관련 에러가 해결되는 거지?
    //TODO : image를 리스트로 바꾸기
    public ArchiveResponse.archiveResponse getArchiveDetail(Long archiveIdx){
        Archive archive = archiveRepository.findByArchiveIdx(archiveIdx);
        ArchiveResponse.archiveResponse response = new ArchiveResponse.archiveResponse(
                        archive.getArchiveIdx(),
                        archive.getCategory().getCategoryIdx(),
                        archive.getCategory().getCategoryName(),
                        archive.getTeam().getTeamIdx(),
                        archive.getUser().getUserIdx(),
                        archive.getTitle(),
                        archive.getContent(),
                        archive.getUrl(),
                        archive.getImageUrl(),
                        archive.isPinned());
        return response;
    }

    @Transactional
    public void deleteArchive(Long archiveIdx){
        Archive archive = archiveRepository.getReferenceById(archiveIdx);
        imageRepository.deleteByArchive(archive);
        archiveRepository.deleteByArchiveIdx(archiveIdx);
    }

}
