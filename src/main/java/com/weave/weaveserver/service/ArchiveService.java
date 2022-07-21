package com.weave.weaveserver.service;

import com.weave.weaveserver.domain.Archive;
import com.weave.weaveserver.domain.Category;
import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.repository.ArchiveRepository;
import com.weave.weaveserver.repository.CategoryRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArchiveService {
    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final CategoryRepository categoryRepository;
    public final ArchiveRepository archiveRepository;

    public void addArchive(ArchiveRequest.createRequest request){
        //TODO : repository 내용 작성하고 repository들 이용해서 addArchive 작성
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

}
