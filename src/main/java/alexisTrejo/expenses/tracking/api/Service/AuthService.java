package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;

public interface AuthService {
    Result<Void> validateRegisterCredentials(UserInsertDTO userInsertDTO);
    String ProcessRegister(UserDTO userDTO);

    Result<UserDTO> validateLoginCredentials(LoginDTO loginDTO);
    String ProcessLogin(UserDTO userDTO);
}
