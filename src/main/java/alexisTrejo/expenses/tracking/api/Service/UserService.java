package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import alexisTrejo.expenses.tracking.api.Utils.Result;

public interface UserService {
    Result<UserDTO> getUserById(Long userId);
    Result<ProfileDTO> getProfileById(Long userId);

    UserDTO createUser(UserInsertDTO userInsertDTO, Role role);
    Result<Void> updateUser(Long userId, UserInsertDTO userInsertDTO);
    Result<Void> deleteUserById(Long userId);
}
