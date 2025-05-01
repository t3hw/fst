package dev.t3hw.fstest.services;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.t3hw.fstest.model.CreateFileDTO;
import dev.t3hw.fstest.model.CreateResponseDTO;
import dev.t3hw.fstest.model.FileDTO;
import dev.t3hw.fstest.server.FileApiDelegate;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileApiService implements FileApiDelegate {

    // private final PostsRepository postsRepo;
    // private final PostsDTOMapper postsMapper;

    
    @Override
    public ResponseEntity<CreateResponseDTO> addFile(CreateFileDTO createFileDTO) {
        // TODO Auto-generated method stub
        return FileApiDelegate.super.addFile(createFileDTO);
    }

    @Override
    public ResponseEntity<FileDTO> getBiggesttFile() {
        // TODO Auto-generated method stub
        return FileApiDelegate.super.getBiggesttFile();
    }

    @Override
    public ResponseEntity<Integer> getFileSize(
            Optional<@Pattern(regexp = "^([\\w\\-. ]{1,32})((/[\\w\\-. ]{1,32}))*$") String> path) {
        // TODO Auto-generated method stub
        return FileApiDelegate.super.getFileSize(path);
    }

}
