package za.co.entelect.pojo.pc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.pojo.enums.Status;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CheckStatus {

    private Long checkStatusId;
    private Status status;
    private String description;

}
