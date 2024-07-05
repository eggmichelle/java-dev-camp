package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "qualifying_customer_types", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "qualifying_customer_types_id"))
@Getter
@Setter
@ToString
public class QualifyingCustomerTypesEntity extends IdentifiableEntity {

    @Column(name = "customer_types_id")
    private Long customerTypesId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

}
