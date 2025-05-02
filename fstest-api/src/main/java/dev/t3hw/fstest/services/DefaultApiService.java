package dev.t3hw.fstest.services;

import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.t3hw.fstest.filesystem.FileSystem;
import dev.t3hw.fstest.filesystem.FileSystemNode;
import dev.t3hw.fstest.filesystem.fsobjects.Directory;
import dev.t3hw.fstest.filesystem.fsobjects.File;
import dev.t3hw.fstest.model.DirectoryDTO;
import dev.t3hw.fstest.model.FileDTO;
import dev.t3hw.fstest.model.GetResponseDTO;
import dev.t3hw.fstest.server.DefaultApiDelegate;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultApiService implements DefaultApiDelegate {
    
    private final FileSystem fileSystem;

    @Override
    public ResponseEntity<Void> delete(String path, Optional<Boolean> recursive) {
        fileSystem.delete(path, recursive.orElse(false));

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetResponseDTO> getFileSystem(
            Optional<@Pattern(regexp = "^(/[\\w\\-. ]{1,32})+$") String> pathOpt) {
        
        List<FileSystemNode> fileSystemNodes;
        if (pathOpt.isPresent()) {
            String path = pathOpt.get();
            fileSystemNodes = fileSystem.getFilesInDirectory(path);
        } else {
            fileSystemNodes = fileSystem.getAllFileSystem();
        }

        LinkedHashMap<String, GetResponseDTO> directories = new LinkedHashMap<>();

        Optional<FileDTO> firstFile;
        try {
            firstFile = Optional.ofNullable(fileSystemNodes.stream()
                    .map(node -> {
                        if (node instanceof Directory dir) {
                            var dto = new DirectoryDTO()
                                    .name(dir.getName())
                                    .createdAt(dir.getCreationTime().atOffset(ZoneOffset.UTC));

                            loadDirToTempCache(directories, dir, dto);
                        } else if (node instanceof File file) {
                            var f = new FileDTO()
                                    .name(file.getName())
                                    .size(file.getSize())
                                    .createdAt(file.getCreationTime().atOffset(ZoneOffset.UTC));
                            var parentDir = ((DirectoryDTO) directories.get(file.getPath()));
                            if (parentDir != null) {
                                parentDir.addSubItemsItem(f);
                            }
                            return f;
                        }
                        return node;
                    }).filter(f -> f instanceof FileDTO)
                    .map(f -> (FileDTO) f)
                    .toList().get(0));
        }   catch (IndexOutOfBoundsException e) {
            firstFile = Optional.empty();
        }

        var firstFilefinal = firstFile;

        var res = Optional.ofNullable(directories.firstEntry()).map(d -> d.getValue());

        return ResponseEntity.ok(res.or(() -> firstFilefinal).get());
    }

    private void loadDirToTempCache(LinkedHashMap<String, GetResponseDTO> directories, Directory dir, DirectoryDTO dto) {
        String key;
        if (dir.getName().equals("home")) {
            key = dir.getPath();
            directories.put(key, dto);
        } else {
            key = dir.getPath() + "/" + dir.getName();
            directories.put(key, dto);
            var parentDir = ((DirectoryDTO) directories.get(dir.getPath()));
            if (parentDir != null) {
                parentDir.addSubItemsItem(dto);
            }
        }
    }
    
}
