package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Models.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-22T15:15:01-0600",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.9.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User insertDtoToEntity(UserInsertDTO userInsertDTO) {
        if ( userInsertDTO == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( userInsertDTO.getEmail() );
        user.setPassword( userInsertDTO.getPassword() );
        user.setFirstName( userInsertDTO.getFirstName() );
        user.setLastName( userInsertDTO.getLastName() );
        user.setDepartment( userInsertDTO.getDepartment() );

        user.setCreatedAt( java.time.LocalDateTime.now() );
        user.setUpdatedAt( java.time.LocalDateTime.now() );

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
    public void updateUser(User user, UserInsertDTO userInsertDTO) {
        if ( userInsertDTO == null ) {
            return;
        }

        user.setEmail( userInsertDTO.getEmail() );
        user.setPassword( userInsertDTO.getPassword() );
        user.setFirstName( userInsertDTO.getFirstName() );
        user.setLastName( userInsertDTO.getLastName() );
        user.setDepartment( userInsertDTO.getDepartment() );
    }
}
