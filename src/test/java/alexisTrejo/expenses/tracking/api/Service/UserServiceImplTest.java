package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Mappers.UserMapper;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Factory.TestDataFactory;
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

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createUser();
        testUserDTO = TestDataFactory.createUserDTO();
        testUserInsertDTO = TestDataFactory.createUserInsertDTO();
        testProfileDTO = TestDataFactory.createProfileDTO();
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
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        verify(userRepository).saveAndFlush(any(User.class));
        verify(userMapper).insertDtoToEntity(testUserInsertDTO);
        verify(userMapper).entityToDTO(testUser);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userMapper.entityToDTO(testUser)).thenReturn(testUserDTO);

        // Act
        Result<UserDTO> result = userService.getUserById(testUser.getId());

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(testUser.getEmail(), result.getData().getEmail());
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act
        Result<UserDTO> result = userService.getUserById(testUser.getId());

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result);
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void getProfileById_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userMapper.entityToProfileDTO(testUser)).thenReturn(testProfileDTO);

        // Act
        Result<ProfileDTO> result = userService.getProfileById(testUser.getEmail());

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(testUser.getEmail(), result.getData().getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void getProfileById_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // Act
        Result<ProfileDTO> result = userService.getProfileById(testUser.getEmail());

        // Assert
        assertFalse(result.isSuccess());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        // Act
        Result<Void> result = userService.updateUser(testUser.getId(), testUserInsertDTO);

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository).findById(testUser.getId());
        verify(userMapper).updateUser(testUser, testUserInsertDTO);
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act
        Result<Void> result = userService.updateUser(testUser.getId(), testUserInsertDTO);

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result);
        verify(userRepository).findById(testUser.getId());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteUserById_WhenUserExists_ShouldReturnSuccessResult() {
        // Arrange
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        // Act
        Result<Void> result = userService.deleteUserById(testUser.getId());

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository).existsById(testUser.getId());
        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void deleteUserById_WhenUserDoesNotExist_ShouldReturnErrorResult() {
        // Arrange
        when(userRepository.existsById(testUser.getId())).thenReturn(false);

        // Act
        Result<Void> result = userService.deleteUserById(testUser.getId());

        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result);
        verify(userRepository).existsById(testUser.getId());
        verify(userRepository, never()).deleteById(any());
    }
}
