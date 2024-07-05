package za.co.entelect.entities.us;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "users", schema = "us")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@Getter
@Setter
public class UserEntity extends IdentifiableEntity {

    private String email;
    private String password;

}