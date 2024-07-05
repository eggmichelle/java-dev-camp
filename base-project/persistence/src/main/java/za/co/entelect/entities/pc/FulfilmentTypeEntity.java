package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "fulfilment_type", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "fulfilment_type_id"))
@Getter
@Setter
@ToString
public class FulfilmentTypeEntity extends IdentifiableEntity {

    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

}
