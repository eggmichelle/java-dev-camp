package za.co.entelect.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import za.co.entelect.ContractHouse;
import za.co.entelect.InsuranceHouse;
import za.co.entelect.InvestmentProductHouse;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.Order;
import za.co.entelect.pojo.pc.Product;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductHousesService {

    @Autowired
    private final InsuranceHouse insuranceHouse;

    @Autowired
    private final ContractHouse contractHouse;

    @Autowired
    private final InvestmentProductHouse investmentProductHouse;

    @Autowired
    private final CustomerMapper customerMapper;

    public ResponseEntity<Order> createPendingAccounts(List<Product> products, Customer customer) {
        for (Product product : products) {
            boolean successful;
            if (product.getName().contains("Insurance")) {
                successful = insuranceHouse.createPendingAccount(product, customer);
            } else if (product.getName().contains("Contract")) {
                successful = contractHouse.createPendingAccount(product, customer);
            } else if (product.getName().contains("Investment Product")) {
                successful = investmentProductHouse.createPendingAccount(product, customer);
            } else {
                log.warn("Product house does not exist for product with name: {}", product.getName());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (!successful) {
                log.warn("Product house could not create account");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        }
        return null;
    }
}
