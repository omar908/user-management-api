package com.omar.user_management_api;

import com.omar.user_management_api.domain.User;
import com.omar.user_management_api.repository.UserRepository;
import com.omar.user_management_api.service.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl sut;

    @DisplayName("This test is a negative case of adding a user, should throw exception when user already exists")
    @Test
    public void shouldThrowWhenUserAlreadyExistsWhenAdding(){
        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(
                Optional.of(new User(UUID.randomUUID(), "name", "test@example.com", Instant.now()))
        );
        Assertions.assertThatThrownBy(() -> sut.createUser("name", "test@example.com")).isInstanceOf(IllegalStateException.class);
        verify(userRepository, times(1)).findByEmail(ArgumentMatchers.anyString());
        verify(userRepository, never()).save(ArgumentMatchers.any());
    }

    @DisplayName("This test is a happy path of adding a user, should save normalized user when user does not exist")
    @Test
    public void shouldAddNormalizedUserWhenUserNotExists(){
        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(
                Optional.empty()
        );

        Assertions.assertThatNoException().isThrownBy(() -> sut.createUser("  Name  ", "  TEST@example.com  "));

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        Assertions.assertThat(savedUser.getName()).isEqualTo("Name");
        Assertions.assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(ArgumentMatchers.any());
    }

    @DisplayName("This test is a negative case, should throw exception when user does not exist")
    @Test
    public void shouldThrowWhenUserNotExistsWhenUpdating(){

        when(userRepository.findById(ArgumentMatchers.any())).thenReturn(
                Optional.empty()
        );
        Assertions.assertThatThrownBy(() -> sut.updateUser(UUID.randomUUID(), "name", "test@example.com")).isInstanceOf(IllegalStateException.class);
        verify(userRepository, times(1)).findById(ArgumentMatchers.any());
        verify(userRepository, never()).updateExistingUser(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(userRepository, never()).removeEmailMapping(ArgumentMatchers.anyString());
        verify(userRepository, never()).addEmailMapping(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @DisplayName("This test is a happy path of updating a user, should update user and keep id and created date")
    @Test
    public void shouldUpdateWhenUserAlreadyExists(){
        var existingUserId = UUID.randomUUID();
        var existingUser = Optional.of(new User(existingUserId, "name", "test@example.com", Instant.now()));

        when(userRepository.findById(ArgumentMatchers.any())).thenReturn(existingUser);
        User updatedUser = sut.updateUser(existingUserId, "newName ", " newEmail@example.com");

        Assertions.assertThat(updatedUser.getName()).isEqualTo("newName");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        Assertions.assertThat(updatedUser.getId()).isEqualTo(existingUserId);
        Assertions.assertThat(updatedUser.getCreatedAt()).isEqualTo(existingUser.get().getCreatedAt());

        verify(userRepository, times(1)).findById(ArgumentMatchers.any());
        verify(userRepository, times(1)).updateExistingUser(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(userRepository, times(1)).removeEmailMapping(ArgumentMatchers.anyString());
        verify(userRepository, times(1)).addEmailMapping(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @DisplayName("This test is a happy path of updating a user, should update user and keep id and created date. However, if email is the same no update is required for email.")
    @Test
    public void shouldUpdateWhenUserAlreadyExistsSameEmail(){
        var existingUserId = UUID.randomUUID();
        var existingUserEmail = "test@example.com";
        var existingUser = Optional.of(new User(existingUserId, "name", existingUserEmail, Instant.now()));

        when(userRepository.findById(ArgumentMatchers.any())).thenReturn(existingUser);
        User updatedUser = sut.updateUser(existingUserId, "newName ", existingUserEmail);

        Assertions.assertThat(updatedUser.getName()).isEqualTo("newName");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(existingUserEmail);
        Assertions.assertThat(updatedUser.getId()).isEqualTo(existingUser.get().getId());
        Assertions.assertThat(updatedUser.getCreatedAt()).isEqualTo(existingUser.get().getCreatedAt());

        verify(userRepository, times(1)).findById(ArgumentMatchers.any());
        verify(userRepository, times(1)).updateExistingUser(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(userRepository, never()).removeEmailMapping(ArgumentMatchers.anyString());
        verify(userRepository, never()).addEmailMapping(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
