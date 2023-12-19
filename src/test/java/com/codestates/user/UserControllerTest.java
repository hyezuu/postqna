package com.codestates.user;

import com.codestates.user.user.controller.UserController;
import com.codestates.user.user.dto.UserDto;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.mapper.UserMapper;
import com.codestates.user.user.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper mapper;

    @Autowired
    private Gson gson;

    @Test
    public void postUserTest() throws Exception {
        //given
        UserDto.Post post = new UserDto.Post("hgd@gmail.com",
                "홍길동",
                "010-1234-5678");
        String content = gson.toJson(post);

        given(mapper.userPostDtoToUser(Mockito.any(UserDto.Post.class)))
                .willReturn(new User());

        User mockResultUser = new User();
        mockResultUser.setUserId(1L);
        given(userService.createUser(Mockito.any(User.class))).willReturn(mockResultUser);
        //when
        ResultActions actions =
                mockMvc.perform(
                        post("/v1/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/v1/users/"))));
    }

}
