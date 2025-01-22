package alexisTrejo.expenses.tracking.api.Service.Factory;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Utils.enums.Role;

import java.time.LocalDateTime;

public class TestDataFactory {

    public static final Long USER_ID = 1L;
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";

    public static User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.EMPLOYEE);
        user.setDepartment("Engineering");
        user.setLastLogin(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);
        return user;
    }

    public static UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(USER_ID);
        userDTO.setEmail(TEST_EMAIL);
        userDTO.setPassword(TEST_PASSWORD);
        userDTO.setRole(Role.EMPLOYEE);
        userDTO.setActive(true);
        return userDTO;
    }

    public static UserInsertDTO createUserInsertDTO() {
        UserInsertDTO userInsertDTO = new UserInsertDTO();
        userInsertDTO.setEmail(TEST_EMAIL);
        userInsertDTO.setPassword(TEST_PASSWORD);
        return userInsertDTO;
    }

    public static ProfileDTO createProfileDTO() {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setEmail(TEST_EMAIL);
        return profileDTO;
    }

    public static User createUserWithProfile() {
        User user = createUser();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.EMPLOYEE);
        return user;
    }

    public static ProfileDTO createValidProfileDTO() {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setEmail(TEST_EMAIL);
        return profileDTO;
    }
}
