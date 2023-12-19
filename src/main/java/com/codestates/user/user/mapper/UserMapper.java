package com.codestates.user.user.mapper;

import com.codestates.user.user.dto.UserDto;
import com.codestates.user.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userPostDtoToUser(UserDto.Post requestBody);

    User userPatchDtoToUser(UserDto.Patch requestBody);

    UserDto.Response userToUserResponse(User user);

    List<UserDto.Response> usersToUserResponses(List<User> users);
}
