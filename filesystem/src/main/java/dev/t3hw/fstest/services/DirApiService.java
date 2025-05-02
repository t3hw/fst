package dev.t3hw.fstest.services;

import java.net.URI;
import java.time.ZoneOffset;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.t3hw.fstest.filesystem.FileSystem;
import dev.t3hw.fstest.model.CreateDirectoryDTO;
import dev.t3hw.fstest.model.CreateResponseDTO;
import dev.t3hw.fstest.server.DirApiDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirApiService implements DirApiDelegate {
    
    private final FileSystem fileSystem;

    @Override
    public ResponseEntity<CreateResponseDTO> addDir(CreateDirectoryDTO createDirectoryDTO) {
        var dir = fileSystem.addDirectory(createDirectoryDTO.getPath(), createDirectoryDTO.getName());

        String dirName = dir.getPath()+"/"+dir.getName();
        var createTime = dir.getCreationTime().atOffset(ZoneOffset.UTC);
        return ResponseEntity.created(URI.create(dirName))
                .body(new CreateResponseDTO(dirName, createTime));

    }
}
