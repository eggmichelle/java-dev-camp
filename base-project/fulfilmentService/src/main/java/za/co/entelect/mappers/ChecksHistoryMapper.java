package za.co.entelect.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.ChecksHistoryEntity;
import za.co.entelect.pojo.us.PastCheck;

@Component
@AllArgsConstructor
public class ChecksHistoryMapper {

    public PastCheck toDomain(ChecksHistoryEntity checksHistoryEntity) {
        return new PastCheck(
                checksHistoryEntity.getId(),
                checksHistoryEntity.getCustomerId(),
                checksHistoryEntity.getCheckDate(),
                checksHistoryEntity.getCheckType()
        );
    }

    public ChecksHistoryEntity toEntity(PastCheck pastCheck) {
        ChecksHistoryEntity checksHistory = new ChecksHistoryEntity();

        checksHistory.setId(pastCheck.getId());
        checksHistory.setCustomerId(pastCheck.getCustomerId());
        checksHistory.setCheckDate(pastCheck.getCheckDate());
        checksHistory.setCheckType(pastCheck.getCheckType());

        return checksHistory;
    }
}
