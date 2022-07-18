package com.weave.weaveserver.service;

import com.weave.weaveserver.dto.ArchiveRequest;
import com.weave.weaveserver.repository.ArchiveRepository;
import com.weave.weaveserver.repository.TeamRepository;
import com.weave.weaveserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArchiveService {
    public final UserRepository userRepository;
    public final TeamRepository teamRepository;
    public final ArchiveRepository archiveRepository;

    public void addArchive(ArchiveRequest.createRequest request){
        //TODO : repository 내용 작성하고 repository들 이용해서 addArchive 작성
    }

}
