package za.co.entelect.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.ChecksHistoryEntity;
import za.co.entelect.mappers.ChecksHistoryMapper;
import za.co.entelect.pojo.enums.CheckType;
import za.co.entelect.pojo.us.PastCheck;
import za.co.entelect.repository.ChecksHistoryRepository;

import java.time.LocalDate;

@Slf4j
@Service
@AllArgsConstructor
public class ChecksHistoryService {

    @Autowired
    private final ChecksHistoryRepository checksHistoryRepository;

    @Autowired
    private final ChecksHistoryMapper checksHistoryMapper;

    public void saveCheck(Long customerId, LocalDate checkDate, CheckType checkType) {
        PastCheck pastCheck = new PastCheck();

        pastCheck.setCustomerId(customerId);
        pastCheck.setCheckDate(checkDate);
        pastCheck.setCheckType(checkType);

    try {
        checksHistoryRepository.save(checksHistoryMapper.toEntity(pastCheck));
    } catch (Exception ex) {
        log.error("Error occurred while saving new check: {}", ex.getMessage());
    }
}

    public PastCheck getHistory(Long customerId, CheckType checkType) {
        ChecksHistoryEntity checksHistoryEntity = checksHistoryRepository.findByCustomerIdAndCheckType(customerId,checkType);
        log.info("ENTITY {}", checksHistoryEntity);

        if (checksHistoryEntity == null) {
            log.info("Customer with id {} has no history of {} check", customerId, checkType);
            return null;
        }

        log.info("Customer has history of {} check", checkType);
        return checksHistoryMapper.toDomain(checksHistoryEntity);
    }

    public void updateCheck(PastCheck pastCheck) {
        pastCheck.setCheckDate(LocalDate.now());
        checksHistoryRepository.save(checksHistoryMapper.toEntity(pastCheck));
    }

}
