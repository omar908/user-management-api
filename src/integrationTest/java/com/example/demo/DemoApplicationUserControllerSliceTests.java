package com.example.demo;

import com.example.demo.config.DemoProperties;
import com.example.demo.service.UserService;
import com.example.demo.web.UserController;
import com.example.demo.domain.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.datafaker.Faker;

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

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	UserService userService;

	private static final Faker faker = new Faker();

	static Supplier<User> userSupplier = () -> new User(
			UUID.randomUUID(),
			faker.name().fullName(),
			faker.funnyName().name().concat("@example.com"),
			Instant.now()
	);

	String createUserJsonOkBodyRequest = "{" +
											"\"name\":\"firstname lastname\"," +
											"\"email\":\"email@example.com\"" +
										"}";

	String createUserJsonBadBodyRequest = "{" +
											"\"email\":\"email@example.com\"" +
										"}";

	static Stream<Arguments> listOfFiveRandomUser() {
		return Stream.of(
				Arguments.of(
						IntStream.range(0,5).mapToObj(i -> userSupplier.get()).toList()
				)
		);
	}

	@ParameterizedTest()
	@MethodSource("listOfFiveRandomUser")
	public void userControllerGetUsersTestSuccess(List<User> users) throws Exception {
		when(userService.listUsers()).thenReturn(users);
		var response = mockMvc.perform(get("/api/users"));
		var mvcResponseBody = response.andReturn().getResponse().getContentAsString();

		List<User> usersFromResponse = objectMapper.readValue(mvcResponseBody, new TypeReference<List<User>>() {});

		response.andExpect(status().isOk());
        Assertions.assertFalse(usersFromResponse.isEmpty());
		IntStream.range(0, users.size()).forEach(
				index -> Assertions.assertEquals(usersFromResponse.get(index), users.get(index))
		);
	}

	@Test
	public void userControllerGetUsersEmpty() throws Exception {
		when(userService.listUsers()).thenReturn(Collections.emptyList());
		var response = mockMvc.perform(get("/api/users"));
		var mvcResponseBody = response.andReturn().getResponse().getContentAsString();

		List<User> usersFromResponse = objectMapper.readValue(mvcResponseBody, new TypeReference<List<User>>() {});

		response.andExpect(status().isOk());
		Assertions.assertTrue(usersFromResponse.isEmpty());
	}

	@Test
	public void userControllerGetUserTestSuccess() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.getUser(any())).thenReturn(Optional.of(userSupplier.get()));
		var response = mockMvc.perform(get("/api/users/"+randomUUID));
		response.andExpect(status().isOk());
	}

	@Test
	public void userControllerGetUserTestFail() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.getUser(any())).thenReturn(Optional.empty());
		var response = mockMvc.perform(get("/api/users/"+randomUUID));
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
	public void userControllerDeleteUserTestNoUserFound() throws Exception {
		var randomUUID = UUID.randomUUID();
		when(userService.deleteUser(any())).thenReturn(false);
		var response = mockMvc.perform(delete("/api/users/"+randomUUID));
		response.andExpect(status().isNoContent());
	}

}
