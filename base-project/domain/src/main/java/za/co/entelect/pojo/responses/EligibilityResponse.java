package za.co.entelect.pojo.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class EligibilityResponse {
    @Getter
    private boolean eligible;
    private ArrayList<String> eligibleAccountTypes;
    private ArrayList<String> matchingAccountTypes;
    private ArrayList<String> eligibleCustomerTypes;
    private String matchingCustomerType;

    public EligibilityResponse() {
        eligible = false;
        matchingAccountTypes = new ArrayList<>();
        matchingCustomerType = "None";
        eligibleAccountTypes = new ArrayList<>();
        eligibleCustomerTypes = new ArrayList<>();
    }
}