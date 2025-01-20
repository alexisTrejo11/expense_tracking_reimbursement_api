package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;

public interface AuthService {
    Result<Void> validateRegisterCredentials(UserInsertDTO userInsertDTO);
    String ProcessRegister(UserDTO userDTO);

    Result<UserDTO> validateLoginCredentials(LoginDTO loginDTO);
    String processLogin(UserDTO userDTO);
}
