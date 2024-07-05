package za.co.entelect.entities.cis.testing;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "customer", schema = "cis")
@AttributeOverride(name = "id", column = @Column(name = "customer_id"))
@Getter
@Setter
@ToString
public class CustomerEntity extends IdentifiableEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "customer_types_id")
    private Long customerTypesId;
}
