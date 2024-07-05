package za.co.entelect.services.us;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.us.UserEntity;
import za.co.entelect.mappers.us.UserMapper;
import za.co.entelect.pojo.us.User;
import za.co.entelect.repository.us.UserRepository;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    public User createUser(String email, String password) {
        User user = new User(email, password);
        Long id = userRepository.save(userMapper.toEntity(user)).getId();
        user.setId(id);
        return user;
    }

    public boolean isEmailUnique(String email) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        return existingUser.isEmpty();
    }

}