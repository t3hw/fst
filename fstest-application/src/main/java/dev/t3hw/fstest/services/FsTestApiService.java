package dev.t3hw.fstest.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.t3hw.fstest.mappers.PostsDTOMapper;
// import dev.t3hw.fstest.db.jpa.repository.PostsJpaRepo;
import dev.t3hw.fstest.db.repository.PostsRepository;
import dev.t3hw.fstest.db.repository.PostsRepository.VoteAction;
import dev.t3hw.fstest.exceptions.CustomExceptions;
import dev.t3hw.fstest.model.CreatePostDTO;
import dev.t3hw.fstest.model.PostDTO;
import dev.t3hw.fstest.server.FstestApiDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FsTestApiService implements FstestApiDelegate {

    private final PostsRepository postsRepo;
    private final PostsDTOMapper postsMapper;

    @Override
    public ResponseEntity<List<PostDTO>> getPosts() {
        return getPostsWithJooq();

        // return getPostsWithJpa();
    }

    private ResponseEntity<List<PostDTO>> getPostsWithJooq() {
        var posts = postsRepo.findAll();
        return ResponseEntity.ok().body(postsMapper.toDTOs(posts));
    }

    @Override
    // public ResponseEntity<IdDTO> createPost(CreatePostDTO createPostDTO) {
    public ResponseEntity<PostDTO> createPost(CreatePostDTO createPostDTO) {
        var createdPost = postsRepo.create(postsMapper.fromDTO(createPostDTO));
        // var res = new IdDTO(createdPost.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postsMapper.toDTO(createdPost));
    }


    @Override
    @Transactional
    public ResponseEntity<PostDTO> upvotePost(Long id, String action) {
        
        VoteAction voteAction;
        try {
            voteAction = VoteAction.fromString(action);
        } catch (Exception e) {
            throw new CustomExceptions.ParsingException("Invalid action: " + action);
        }

        var exists = postsRepo.existsByIdForUpdate(id);

        if (!exists) {
            throw new CustomExceptions.NotFoundException("Post not found with id: " + id);
        }

        var post = postsRepo.findById(id);

        var updatedPost = postsRepo.updateVoteCount(post, voteAction);

        return ResponseEntity.ok(postsMapper.toDTO(updatedPost));
    }


    // private final PostsJpaRepo postsJpaRepo;

    // private ResponseEntity<List<PostDTO>> getPostsWithJpa() {
    //     var posts = postsJpaRepo.findAll();
    //     return ResponseEntity.ok(posts.stream().map(post -> {
    //         var postDTO = new PostDTO();
    //         postDTO.setId(post.getId());
    //         postDTO.setContent(post.getContent());
    //         return postDTO;
    //     }).toList());
    // }
}
