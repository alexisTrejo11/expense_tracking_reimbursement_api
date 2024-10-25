package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    public static User getTestUser() {
        User testUser = new User();
        testUser.setEmail("test.user@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setLastLogin(LocalDateTime.now());
        testUser.setActive(Boolean.TRUE);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setRole(Role.EMPLOYEE);
        testUser.setPassword("password");
        testUser.setDepartment("department test");
        return testUser;
    }

    @Test
    public void whenSave_thenFindById() {
        User testUser = getTestUser();
        userRepository.save(testUser);

        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals(testUser.getEmail(), found.get().getEmail());
    }

    @Test
    public void whenUpdate_thenUserShouldBeUpdated() {
        User testUser = getTestUser();
        userRepository.save(testUser);

        // Update user
        testUser.setLastName("Updated LastName");
        userRepository.save(testUser);

        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated LastName", found.get().getLastName());
    }

    @Test
    public void whenDelete_thenUserShouldNotBeFound() {
        User testUser = getTestUser();
        userRepository.save(testUser);

        // Delete user
        userRepository.deleteById(testUser.getId());

        Optional<User> found = userRepository.findById(testUser.getId());
        assertFalse(found.isPresent());
    }

    @Test
    public void whenFindAll_thenAllUsersShouldBeReturned() {
        User testUser1 = getTestUser();
        User testUser2 = getTestUser();
        testUser2.setEmail("test.user2@example.com");
        userRepository.save(testUser1);
        userRepository.save(testUser2);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }
}
