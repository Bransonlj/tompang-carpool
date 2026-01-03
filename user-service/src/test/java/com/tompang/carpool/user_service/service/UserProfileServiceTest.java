package com.tompang.carpool.user_service.service;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.exception.ResourceNotFoundException;
import com.tompang.carpool.user_service.mapper.UserProfileMapper;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserProfileServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private  S3Service s3Service;

    @Mock
    private UserProfileMapper mapper;

    @InjectMocks
    private UserProfileService userProfileService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class GetUserProfileByIdTests {
        @Test
        void returnsUserProfileDtoWithPictureUrlSuccessfully() {
            String profilePictureUrl = "test-profile-picture.com";
            String userId = "u123";
            UserProfileDto dto = new UserProfileDto(
                    userId, "Bob", "Tan", "Bob Tan",
                    "user@email.com", null, profilePictureUrl
            );
            User user = User
                    .builder()
                    .id(userId)
                    .email("user@email.com")
                    .hasProfilePicture(true)
                    .build();
            when(userRepository.findById(anyString()))
                    .thenReturn(Optional.of(user));
            when(mapper.toDto(any(User.class), anyString())).thenReturn(dto);
            when(s3Service.getFileUrl(anyString(), any(S3Service.Directory.class))).thenReturn(profilePictureUrl);

            // act
            UserProfileDto dtoResult = userProfileService.getUserProfileById(userId);

            // assert
            verify(userRepository).findById(eq(userId));
            verify(s3Service).getFileUrl(eq(userId), eq(S3Service.Directory.PROFILE_PICTURE));
            verify(mapper).toDto(eq(user), eq(profilePictureUrl));
            assertThat(dtoResult).isEqualTo(dto);
        }

        @Test
        void returnsUserProfileDtoWithoutPictureUrlSuccessfully() {
            String userId = "u123";
            UserProfileDto dto = new UserProfileDto(
                    userId, "Bob", "Tan", "Bob Tan",
                    "user@email.com", null, null
            );
            User user = User
                    .builder()
                    .id(userId)
                    .email("user@email.com")
                    .hasProfilePicture(false)
                    .build();
            when(userRepository.findById(anyString()))
                    .thenReturn(Optional.of(user));
            when(mapper.toDto(any(User.class))).thenReturn(dto);

            // act
            UserProfileDto dtoResult = userProfileService.getUserProfileById(userId);

            // assert
            verify(userRepository).findById(eq(userId));
            verify(s3Service, never()).getFileUrl(any(), any());
            verify(mapper).toDto(eq(user));
            assertThat(dtoResult).isEqualTo(dto);
        }

        @Test
        void throwsNotFoundException_whenUserIdDoesNotExist() {
            when(userRepository.findById(anyString()))
                    .thenReturn(Optional.empty());
            assertThatThrownBy(() -> userProfileService.getUserProfileById("u123"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("UserProfile not found");
        }
    }

    @Nested
    class UploadUserProfilePictureTests {
        private final MultipartFile file = new MockMultipartFile(
                "file",                       // form field name
                "profile.jpg",                // original filename
                "image/jpeg",                 // content type
                "dummy image content".getBytes()
        );

        @Test
        void uploadsPictureToS3AndUpdatesUserSuccessfully() throws Exception {
            String userId = "u123";
            User user = new User();

            when(userRepository.findById(anyString()))
                    .thenReturn(Optional.of(user));

            // act
            userProfileService.uploadUserProfilePicture(userId, file);

            // assert
            verify(userRepository).findById(eq(userId));
            verify(s3Service).uploadFile(eq(userId), eq(S3Service.Directory.PROFILE_PICTURE), eq(file));
            verify(userRepository).save(eq(user));
            assertThat(user.isHasProfilePicture()).isTrue();
        }

        @Test
        void throwsResourceNotFoundExceptionIfUserDoesNotExist() {
            String userId = "u123";
            when(userRepository.findById(anyString()))
                    .thenReturn(Optional.empty());

            // act + assert
            assertThatThrownBy(() -> userProfileService.uploadUserProfilePicture(userId, file))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("UserProfile not found");
            verify(userRepository).findById(eq(userId));

        }

        @Test
        void throwsExceptionIfS3UploadFailsWithIOException() throws Exception {
            String userId = "u123";
            User user = new User();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            doThrow(new IOException("S3 error"))
                    .when(s3Service)
                    .uploadFile(any(), any(), any());

            // then
            assertThatThrownBy(() -> userProfileService.uploadUserProfilePicture(userId, file))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error uploading image");
            verify(userRepository).findById(eq(userId));
            verify(s3Service).uploadFile(eq(userId), eq(S3Service.Directory.PROFILE_PICTURE), eq(file));
        }
    }

    @Nested
    class GetUserProfilesMappedByIdsTests {

        private List<User> users;
        private UserProfileDto dtoWithPicture;
        private UserProfileDto dtoWithoutPicture;
        private String profilePictureUrl;

        @BeforeEach
        void setup() {
            profilePictureUrl = "test-profile-picture.com";
            users = List.of(
                    User.builder()
                            .id("user1")
                            .email("user@email.com")
                            .hasProfilePicture(false)
                            .build(),
                    User.builder()
                            .id("user2")
                            .email("user@email.com")
                            .hasProfilePicture(true)
                            .build(),
                    User.builder()
                            .id("user3")
                            .email("user@email.com")
                            .hasProfilePicture(false)
                            .build()
            );

            dtoWithPicture = new UserProfileDto(
                    "user-123", "Bob", "Tan", "Bob Tan",
                    "user@email.com", null, profilePictureUrl
            );
            dtoWithoutPicture = new UserProfileDto(
                    "user-123", "Bob", "Tan", "Bob Tan",
                    "user@email.com", null, null
            );

            when(userRepository.findAllById(anyList())).thenReturn(users);
            when(s3Service.getFileUrl(anyString(), any(S3Service.Directory.class))).thenReturn(profilePictureUrl);
            when(mapper.toDto(any(User.class), anyString())).thenReturn(dtoWithPicture);
            when(mapper.toDto(any(User.class))).thenReturn(dtoWithoutPicture);
        }
        @Test
        void returnsMapOfUserIdDtosIncludingPhotoSuccessfully() {
            Map<String, UserProfileDto> expectedResult = new HashMap<>();
            expectedResult.put(users.get(0).getId(), dtoWithoutPicture);
            expectedResult.put(users.get(1).getId(), dtoWithPicture);
            expectedResult.put(users.get(2).getId(), dtoWithoutPicture);

            // act
            Map<String, UserProfileDto> result = userProfileService.getUserProfilesMappedByIds(
                    List.of("u1", "u2"), true);

            // assert
            verify(userRepository).findAllById(eq(List.of("u1", "u2")));
            verify(s3Service).getFileUrl(eq("user2"), eq(S3Service.Directory.PROFILE_PICTURE));
            verify(mapper).toDto(eq(users.get(0)));
            verify(mapper).toDto(eq(users.get(1)), eq(profilePictureUrl));
            verify(mapper).toDto(eq(users.get(2)));
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void returnsMapOfUserIdDtosExcludingPhotoSuccessfully() {
            Map<String, UserProfileDto> expectedResult = new HashMap<>();
            expectedResult.put(users.get(0).getId(), dtoWithoutPicture);
            expectedResult.put(users.get(1).getId(), dtoWithoutPicture);
            expectedResult.put(users.get(2).getId(), dtoWithoutPicture);

            // act
            Map<String, UserProfileDto> result = userProfileService.getUserProfilesMappedByIds(
                    List.of("u1", "u2"), false);

            // assert
            verify(userRepository).findAllById(eq(List.of("u1", "u2")));
            verify(s3Service, never()).getFileUrl(any(), any());
            verify(mapper).toDto(eq(users.get(0)));
            verify(mapper).toDto(eq(users.get(1)));
            verify(mapper).toDto(eq(users.get(2)));
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void returnsEmptyMapIfNoUsersFound() {
            when(userRepository.findAllById(anyList())).thenReturn(List.of());
            Map<String, UserProfileDto> expectedResult = new HashMap<>();

            // act
            Map<String, UserProfileDto> result = userProfileService.getUserProfilesMappedByIds(
                    List.of("u1", "u2"), true);

            // assert
            verify(userRepository).findAllById(eq(List.of("u1", "u2")));
            assertThat(result).isEqualTo(expectedResult);
        }
    }
}
