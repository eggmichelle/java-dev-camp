package za.co.entelect.pojo.us;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.pojo.enums.CheckType;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PastCheck {

    private Long id;
    private Long customerId;
    private LocalDate checkDate;
    private CheckType checkType;

    public PastCheck() {
    }
}