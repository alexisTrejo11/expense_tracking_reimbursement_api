package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastLogin", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    User insertDtoToEntity(UserInsertDTO studentInsertDTO);

    UserDTO entityToDTO(User user);
    ProfileDTO entityToProfileDTO(User user);


    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    void updateUser(@MappingTarget User student, UserInsertDTO studentInsertDTO);
}
