package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.UserMapper;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Middleware.PasswordHandler;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AuthService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JWTSecurity JWTSecurity;

    @Override
    public Result<Void> validateRegisterCredentials(UserInsertDTO userInsertDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userInsertDTO.getEmail());
        if (optionalUser.isPresent()) {
            return Result.error("Email Already Taken");
        }

        return Result.success();
    }

    @Override
    @Transactional
    public String ProcessRegister(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Can't Process Login"));

        return JWTSecurity.generateToken(user.getId(), user.getRole().toString());
    }


    @Override
    public Result<UserDTO> validateLoginCredentials(LoginDTO loginDTO) {
        Optional<User> optionalUser = getUserByEmail(loginDTO.getEmail());
        if (optionalUser.isEmpty()) {
            return Result.error("User With Given Credentials Not Found");
        }

        User user = optionalUser.get();

        boolean isPasswordCorrect = PasswordHandler.validatePassword(loginDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            return Result.error("Wrong Password");
        }

        return Result.success(userMapper.entityToDTO(user));
    }

    @Override
    @Transactional
    public String ProcessLogin(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Cant Process Login"));

        String JWT = JWTSecurity.generateToken(user.getId(), user.getRole().toString());

        user.updateLastLogin();
        userRepository.saveAndFlush(user);

        return JWT;
    }


    @Cacheable(value = "userCredentialsCache", key = "#email")
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
