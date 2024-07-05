package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "cart", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "cart_id"))
@Getter
@Setter
@ToString
public class CartEntity extends IdentifiableEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

}
