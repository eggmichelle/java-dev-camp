package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "order_items", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "order_items_id"))
@Getter
@Setter
@ToString
public class OrderItemsEntity extends IdentifiableEntity {

    String description;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrdersEntity order;

}
