package za.co.entelect.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Getter
public class FulfilmentConfig {

    @Value("${fulfilment.checks.kyc-time-period-days}")
    private int kycTimePeriod;

    @Value("${fulfilment.checks.living-status-time-period-days}")
    private int livingStatusTimePeriod;

    @Value("${fulfilment.checks.duplicate-id-time-period-days}")
    private int duplicateIdTimePeriod;

    @Value("${fulfilment.checks.marital-status-time-period-days}")
    private int maritalStatusTimePeriod;

    @Value("${fulfilment.checks.credit-time-period-days}")
    private int creditTimePeriod;

    @Value("${fulfilment.checks.fraud-time-period-days}")
    private int fraudTimePeriod;

    public Duration getKycTimePeriod() {
        return Duration.ofDays(kycTimePeriod);
    }

    public Duration getLivingStatusTimePeriod() {
        return Duration.ofDays(livingStatusTimePeriod);
    }

    public Duration getDuplicateIdTimePeriod() {
        return Duration.ofDays(duplicateIdTimePeriod);
    }

    public Duration getMaritalStatusTimePeriod() {
        return Duration.ofDays(maritalStatusTimePeriod);
    }

    public Duration getCreditTimePeriod() {
        return Duration.ofDays(creditTimePeriod);
    }

    public Duration getFraudTimePeriod() {
        return Duration.ofDays(fraudTimePeriod);
    }
}

