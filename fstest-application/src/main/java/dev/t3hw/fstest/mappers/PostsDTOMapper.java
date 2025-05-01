package dev.t3hw.fstest.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.t3hw.fstest.db.jooq.tables.pojos.Posts;
import dev.t3hw.fstest.model.CreatePostDTO;
import dev.t3hw.fstest.model.PostDTO;

@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface PostsDTOMapper {
    PostDTO toDTO(Posts post);
    Posts fromDTO(PostDTO postDTO);
    @Mapping(target = "id", ignore = true) @Mapping(target = "votes", ignore = true)
    Posts fromDTO(CreatePostDTO postDTO);
    List<PostDTO> toDTOs(List<Posts> posts);
    List<Posts> fromDTOs(List<PostDTO> postDTOs);
}
