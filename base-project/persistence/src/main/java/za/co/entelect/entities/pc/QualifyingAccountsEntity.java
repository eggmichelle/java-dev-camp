package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

@Entity
@Table(name = "qualifying_accounts", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "qualifying_account_id"))
@Getter
@Setter
@ToString
public class QualifyingAccountsEntity extends IdentifiableEntity {

    @Column(name = "account_type_id")
    private Long accountTypeId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

}
