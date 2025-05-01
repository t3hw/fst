package dev.t3hw.fstest.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.t3hw.fstest.model.CreateDirectoryDTO;
import dev.t3hw.fstest.model.CreateResponseDTO;
import dev.t3hw.fstest.server.DirApiDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirApiService implements DirApiDelegate {

    // private final PostsRepository postsRepo;
    // private final PostsDTOMapper postsMapper;

    @Override
    public ResponseEntity<CreateResponseDTO> addDir(CreateDirectoryDTO createDirectoryDTO) {
        // TODO Auto-generated method stub
        return DirApiDelegate.super.addDir(createDirectoryDTO);
    }
}
