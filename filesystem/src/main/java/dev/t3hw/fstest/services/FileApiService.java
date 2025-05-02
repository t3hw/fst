package dev.t3hw.fstest.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.t3hw.fstest.filesystem.FileSystem;
import dev.t3hw.fstest.filesystem.fsobjects.File;
import dev.t3hw.fstest.model.CreateFileDTO;
import dev.t3hw.fstest.model.CreateResponseDTO;
import dev.t3hw.fstest.model.FileDTO;
import dev.t3hw.fstest.server.FileApiDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileApiService implements FileApiDelegate {

    private final FileSystem fileSystem;
    
    @Override
    public ResponseEntity<CreateResponseDTO> addFile(CreateFileDTO createFileDTO) {
        var file = fileSystem.addFile(createFileDTO.getPath(), createFileDTO.getName(), createFileDTO.getSize());

        OffsetDateTime createTime = file.getCreationTime().atOffset(ZoneOffset.UTC);
        return ResponseEntity.ok(new CreateResponseDTO(createFileDTO.getPath() + "/" + createFileDTO.getName(), createTime));
    }

    @Override
    public ResponseEntity<FileDTO> getBiggesttFile() {
        var file = fileSystem.getBiggestFile();
        
        OffsetDateTime createTime = file.getCreationTime().atOffset(ZoneOffset.UTC);

        return ResponseEntity.ok(new FileDTO()
                .path(file.getPath())
                .name(file.getName())
                .size(file.getSize())
                .createdAt(createTime));
    }

    @Override
    public ResponseEntity<Integer> getFileSize(String path) {
        File file = fileSystem.getFile(path);
        
        return ResponseEntity.ok(file.getSize());
    }

}
