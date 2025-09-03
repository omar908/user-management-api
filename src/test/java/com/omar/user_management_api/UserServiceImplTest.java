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

    @DisplayName("This test is a negative case, should throw exception when user already exists")
    @Test
    public void shouldThrowWhenUserAlreadyExists(){
        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(
                Optional.of(new User(UUID.randomUUID(), "name", "test@example.com", Instant.now()))
        );
        Assertions.assertThatThrownBy(() -> sut.createUser("name", "test@example.com")).isInstanceOf(IllegalStateException.class);
        verify(userRepository, times(1)).findByEmail(ArgumentMatchers.anyString());
        verify(userRepository, never()).save(ArgumentMatchers.any());
    }

    @DisplayName("This test is a happy path of adding a user, should save normalized user when user does not exist")
    @Test
    public void shouldSaveNormalizedUserWhenNotExists(){
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
}
