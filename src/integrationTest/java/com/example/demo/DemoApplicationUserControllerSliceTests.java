package com.example.demo;

import com.example.demo.config.DemoProperties;
import com.example.demo.service.UserService;
import com.example.demo.web.UserController;
import com.example.demo.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableConfigurationProperties(DemoProperties.class)
@WebMvcTest(controllers = UserController.class)
class DemoApplicationUserControllerSliceTests {
	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	UserService userService;

	Supplier<User> userSupplier = () -> new User(
			UUID.randomUUID(),
			"firstname lastname",
			"email@email.com",
			Instant.now()
	);

	String createUserJsonOkBodyRequest = "{" +
											"\"name\":\"firstname lastname\"," +
											"\"email\":\"email@email.com\"" +
										"}";

	String createUserJsonBadBodyRequest = "{" +
											"\"email\":\"email@email.com\"" +
										"}";

	@Test
	public void userControllerGetUsersTestSuccess() throws Exception {
		when(userService.listUsers()).thenReturn(List.of(userSupplier.get()));
		var response = mockMvc.perform(get("/api/users"));
		response.andExpect(status().isOk());
	}

	@Test
	public void userControllerGetUsersEmpty() throws Exception {
		when(userService.listUsers()).thenReturn(Collections.emptyList());
		var response = mockMvc.perform(get("/api/users"));
		response.andExpect(status().isNotFound());
	}

	@Test
	public void userControllerGetUserTestSuccess() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.getUser(any())).thenReturn(Optional.of(userSupplier.get()));
		var response = mockMvc.perform(get("/api/user/"+randomUUID));
		response.andExpect(status().isNotFound());
	}

	@Test
	public void userControllerGetUserTestFail() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.getUser(any())).thenReturn(Optional.empty());
		var response = mockMvc.perform(get("/api/users"+randomUUID));
		response.andExpect(status().isNotFound());
	}

	@Test
	public void userControllerAddUserTestSuccess() throws Exception {
		when(userService.createUser(anyString(), anyString())).thenReturn(userSupplier.get());
		var response = mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createUserJsonOkBodyRequest)
		);
		response.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("BadRequest Missing required fields")
	public void userControllerAddUserTestBadRequest() throws Exception {
		// No need to mock userService.createUser as it will fail at controller level.
		var response = mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createUserJsonBadBodyRequest)
		);
		response.andExpect(status().isBadRequest());
	}

	@Test
	public void userControllerDeleteUserTestSuccess() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.deleteUser(any())).thenReturn(true);
		var response = mockMvc.perform(delete("/api/users/"+randomUUID));
		response.andExpect(status().isNoContent());
	}

	@Test
	public void userControllerDeleteUserTestFail() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.deleteUser(any())).thenReturn(false);
		var response = mockMvc.perform(delete("/api/users"+randomUUID));
		response.andExpect(status().isNotFound());
	}

}
