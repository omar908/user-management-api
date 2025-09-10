package com.omar.user_management_api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omar.user_management_api.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserManagementApiApplicationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Full flow of adding user, updating user, adding multiple users, viewing all users, deleting user")
    @Test
    void fullCrudFlow() throws Exception {
        // create a user
        var createRequest = objectMapper.writeValueAsString(
                Map.of("name", "Alice", "email", "alice@example.com")
        );
        var createResponse1 = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn();

        User user1 = objectMapper.readValue(createResponse1.getResponse().getContentAsString(), User.class);
        assertThat(user1).isNotNull();
        UUID userId1 = user1.getId();

        // update existing user
        var updateRequest = objectMapper.writeValueAsString(
                Map.of("name", "Alice Updated", "email", "alice.updated@example.com")
        );

        var updateResponse = mockMvc.perform(put("/api/users/" + userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andReturn();

        User updatedUser = objectMapper.readValue(updateResponse.getResponse().getContentAsString(), User.class);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Alice Updated");
        assertThat(updatedUser.getEmail()).isEqualTo("alice.updated@example.com");

        // get existing user to confirm update did apply correctly
        var getResponse1 = mockMvc.perform(get("/api/users/" + userId1))
                .andExpect(status().isOk())
                .andReturn();

        User getUser1 = objectMapper.readValue(getResponse1.getResponse().getContentAsString(), User.class);
        assertThat(getUser1).isNotNull();
        assertThat(getUser1.getName()).isEqualTo("Alice Updated");

        // add another user
        var createRequest2 = objectMapper.writeValueAsString(
                Map.of("name", "Bob", "email", "bob@example.com")
        );

        var createResponse2 = mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequest2))
                .andExpect(status().isCreated())
                .andReturn();
        User createUser2 = objectMapper.readValue(createResponse2.getResponse().getContentAsString(), User.class);
        assertThat(createUser2).isNotNull();
        UUID userId2 = createUser2.getId();

        // gets all users and confirms they are co-existing
        var allUsersResponse = mockMvc.perform(get("/api/users")).andExpect(status().isOk()).andReturn();
        var mvcResponseBody = allUsersResponse.getResponse().getContentAsString();
        List<User> allUsers = objectMapper.readValue(mvcResponseBody, new TypeReference<List<User>>() {});

        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting(User::getName)
                .containsExactlyInAnyOrder("Alice Updated", "Bob");

        // deletes first created user
        mockMvc.perform(delete("/api/users/"+userId1)).andExpect(status().isNoContent()).andReturn();

        // confirms first created user is deleted
        mockMvc.perform(get("/api/users/" + userId1))
                .andExpect(status().isNotFound());

        // gets the second user and confirms it is still present
        var getUserResponse2 = mockMvc.perform(get("/api/users/" + userId2))
                .andExpect(status().isOk())
                .andReturn();

        User getUser2 = objectMapper.readValue(getUserResponse2.getResponse().getContentAsString(), User.class);
        assertThat(getUser2).isNotNull();
        assertThat(getUser2.getName()).isEqualTo("Bob");
    }
}
