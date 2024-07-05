package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "cart_item", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "cart_item_id"))
@Getter
@Setter
@ToString
public class CartItemEntity extends IdentifiableEntity {

    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

}

