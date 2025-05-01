package dev.t3hw.fstest.services;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.t3hw.fstest.model.GetResponseDTO;
import dev.t3hw.fstest.server.DefaultApiDelegate;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultApiService implements DefaultApiDelegate {
    
    @Override
    public ResponseEntity<GetResponseDTO> getFileSystem(
            Optional<@Pattern(regexp = "^([\\w\\-. ]{1,32})((/[\\w\\-. ]{1,32}))*$") String> path,
            Optional<Boolean> recursive) {
        // TODO Auto-generated method stub
        return DefaultApiDelegate.super.getFileSystem(path, recursive);
    }

    @Override
    public ResponseEntity<Void> deleteFileSystem(
            Optional<@Pattern(regexp = "^([\\w\\-. ]{1,32})((/[\\w\\-. ]{1,32}))*$") String> path,
            Optional<Boolean> recursive) {
        // TODO Auto-generated method stub
        return DefaultApiDelegate.super.deleteFileSystem(path, recursive);
    }


}
