package za.co.entelect;

import org.springframework.stereotype.Component;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.Product;

@Component
public class ContractHouse {
    public boolean createPendingAccount(Product product, Customer customer) {
        return product.getName().equals("Device Contract");
    }

    public void openAccount(Product product, Customer customer) {
    }

    public void closeAccount(Product product, Customer customer) {
    }

}
