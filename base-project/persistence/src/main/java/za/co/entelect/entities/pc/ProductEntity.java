package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "products", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
@Getter
@Setter
@ToString
public class ProductEntity extends IdentifiableEntity {

    private String name;
    private String description;
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

}
