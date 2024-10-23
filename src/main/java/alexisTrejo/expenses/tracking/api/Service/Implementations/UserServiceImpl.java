package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.UserMapper;
import alexisTrejo.expenses.tracking.api.Middleware.PasswordHandler;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.UserService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO createUser(UserInsertDTO userInsertDTO, Role role) {
        User user = userMapper.insertDtoToEntity(userInsertDTO);
        user.setRole(role);
        user.setPassword(PasswordHandler.hashPassword(userInsertDTO.getPassword()));

        userRepository.saveAndFlush(user);

        return userMapper.entityToDTO(user);
    }

    @Override
    public Result<UserDTO> getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser
                .map(user -> Result.success(userMapper.entityToDTO(user)))
                .orElseGet(() -> Result.error("User with id " + userId + " not found") );
    }

    @Override
    public Result<ProfileDTO> getProfileById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser
                .map(user -> Result.success(userMapper.entityToProfileDTO(user)))
                .orElseGet(() -> Result.error("User with id " + userId + " not found") );
    }

    @Override
    public Result<Void> updateUser(Long userId, UserInsertDTO userInsertDTO) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser
                .map(user -> {
                    userMapper.updateUser(user, userInsertDTO);
                    userRepository.saveAndFlush(user);
                    return Result.success();
                })
                .orElseGet(() -> Result.error("User with id " + userId + " not found"));
    }

    @Override
    public Result<Void> deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            return Result.error("User with id " + userId + " not found");
        }

        userRepository.deleteById(userId);
        return Result.success();
    }
}
