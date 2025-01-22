package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Mappers.UserMapper;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Implementations.UserServiceImpl;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private UserInsertDTO testUserInsertDTO;
    private ProfileDTO testProfileDTO;
    private final Long USER_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(TEST_PASSWORD);

        testUserDTO = new UserDTO();
        testUserDTO.setId(USER_ID);
        testUserDTO.setEmail(TEST_EMAIL);

        testUserInsertDTO = new UserInsertDTO();
        testUserInsertDTO.setEmail(TEST_EMAIL);
        testUserInsertDTO.setPassword(TEST_PASSWORD);

        testProfileDTO = new ProfileDTO();
        testProfileDTO.setEmail(TEST_EMAIL);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        // Arrange
        Role role = Role.EMPLOYEE;
        when(userMapper.insertDtoToEntity(testUserInsertDTO)).thenReturn(testUser);
        when(userMapper.entityToDTO(testUser)).thenReturn(testUserDTO);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.createUser(testUserInsertDTO, role);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        verify(userRepository).saveAndFlush(any(User.class));
        verify(userMapper).insertDtoToEntity(testUserInsertDTO);
        verify(userMapper).entityToDTO(testUser);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userMapper.entityToDTO(testUser)).thenReturn(testUserDTO);

        // Act
        Result<UserDTO> result = userService.getUserById(USER_ID);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(TEST_EMAIL, result.getData().getEmail());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        Result<UserDTO> result = userService.getUserById(USER_ID);

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result);
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void getProfileById_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(userMapper.entityToProfileDTO(testUser)).thenReturn(testProfileDTO);

        // Act
        Result<ProfileDTO> result = userService.getProfileById(TEST_EMAIL);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(TEST_EMAIL, result.getData().getEmail());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void getProfileById_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act
        Result<ProfileDTO> result = userService.getProfileById(TEST_EMAIL);

        // Assert
        assertFalse(result.isSuccess());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        // Act
        Result<Void> result = userService.updateUser(USER_ID, testUserInsertDTO);

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository).findById(USER_ID);
        verify(userMapper).updateUser(testUser, testUserInsertDTO);
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = userService.updateUser(USER_ID, testUserInsertDTO);

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result);
        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteUserById_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        // Act
        Result<Void> result = userService.deleteUserById(USER_ID);

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository).existsById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void deleteUserById_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // Act
        Result<Void> result = userService.deleteUserById(USER_ID);

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result);
        verify(userRepository).existsById(USER_ID);
        verify(userRepository, never()).deleteById(any());
    }
}