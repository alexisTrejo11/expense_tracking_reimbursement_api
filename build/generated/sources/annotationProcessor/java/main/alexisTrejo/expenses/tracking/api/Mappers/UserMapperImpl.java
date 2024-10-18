package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-18T13:02:02-0600",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.9.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User insertDtoToEntity(UserInsertDTO studentInsertDTO) {
        if ( studentInsertDTO == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( studentInsertDTO.getEmail() );
        user.setPassword( studentInsertDTO.getPassword() );
        user.setFirstName( studentInsertDTO.getFirstName() );
        user.setLastName( studentInsertDTO.getLastName() );
        user.setDepartment( studentInsertDTO.getDepartment() );

        user.setCreatedAt( java.time.LocalDateTime.now() );
        user.setUpdatedAt( java.time.LocalDateTime.now() );
        user.setLastLogin( java.time.LocalDateTime.now() );

        return user;
    }

    @Override
    public UserDTO entityToDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail( user.getEmail() );
        userDTO.setFirstName( user.getFirstName() );
        userDTO.setLastName( user.getLastName() );
        userDTO.setDepartment( user.getDepartment() );
        userDTO.setId( user.getId() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setRole( user.getRole() );
        userDTO.setActive( user.getActive() );

        return userDTO;
    }

    @Override
    public ProfileDTO entityToProfileDTO(User user) {
        if ( user == null ) {
            return null;
        }

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setEmail( user.getEmail() );
        profileDTO.setFirstName( user.getFirstName() );
        profileDTO.setLastName( user.getLastName() );
        profileDTO.setDepartment( user.getDepartment() );

        return profileDTO;
    }

    @Override
    public void updateUser(User student, UserInsertDTO studentInsertDTO) {
        if ( studentInsertDTO == null ) {
            return;
        }

        student.setEmail( studentInsertDTO.getEmail() );
        student.setPassword( studentInsertDTO.getPassword() );
        student.setFirstName( studentInsertDTO.getFirstName() );
        student.setLastName( studentInsertDTO.getLastName() );
        student.setDepartment( studentInsertDTO.getDepartment() );

        student.setUpdatedAt( java.time.LocalDateTime.now() );
    }
}
