package za.co.entelect.mappers.us;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.us.UserEntity;
import za.co.entelect.pojo.us.User;

@Component
@AllArgsConstructor
public class UserMapper {

    public User toDomain(UserEntity userEntity) {

        return new User(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword());
    }

    public UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity();

        userEntity.setId(user.getId());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());

        return userEntity;
    }

}
