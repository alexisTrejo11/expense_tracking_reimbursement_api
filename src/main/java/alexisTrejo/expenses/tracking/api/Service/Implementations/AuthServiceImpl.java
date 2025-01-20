package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.UserMapper;
import alexisTrejo.expenses.tracking.api.Auth.JWTService;
import alexisTrejo.expenses.tracking.api.Utils.PasswordHandler;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AuthService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JWTService jwtService;

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

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getId());

        return jwtService.generateToken(extraClaims, getUserDetails(user));
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
    public String processLogin(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Can't Process Login"));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getId());

        String jwt = jwtService.generateToken(extraClaims, getUserDetails(user));

        user.updateLastLogin();
        userRepository.saveAndFlush(user);

        return jwt;
    }

    private UserDetails getUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthorities(user.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }


    @Cacheable(value = "userCredentialsCache", key = "#email")
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
