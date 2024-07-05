package za.co.entelect.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.pojo.enums.CheckType;

import java.time.LocalDate;

@Entity
@Table(name = "checks_history", schema = "us")
@AttributeOverride(name = "id", column = @Column(name = "check_history_id"))
@Getter
@Setter
@ToString
public class ChecksHistoryEntity extends IdentifiableEntity {

    @Column(name = "customer_id")
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type")
    private CheckType checkType;

    @Column(name = "check_date")
    private LocalDate checkDate;

}
