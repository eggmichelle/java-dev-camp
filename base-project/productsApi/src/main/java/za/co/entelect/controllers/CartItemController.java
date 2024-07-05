package za.co.entelect.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.pojo.pc.Cart;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.services.pc.CartItemService;
import za.co.entelect.services.pc.CartService;
import za.co.entelect.services.cis.CustomerService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/cartItems")
public class CartItemController {

    private final CustomerService customerService;
    private final CartService cartService;
    private final CartItemService cartItemService;


    @Autowired
    public CartItemController(CustomerService customerService, CartService cartService, CartItemService cartItemService) {
        this.customerService = customerService;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Cart> addCartItem(@PathVariable Long productId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Customer customer = customerService.getCustomerFromAuth(authorizationHeader);
            Cart cart = cartService.getCartByCustomerId(customer.getId());
            if (cart == null) cart = cartService.createCart(customer.getId());
            cartItemService.createCartItem(cart, productId);
            cart = cartService.getCart(cart.getCartId());

            return ResponseEntity.ok(cart);
        } catch (ApiException e) {
            log.error("Error occurred while adding a cart item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Product>> getCartItems(@PathVariable Long customerId, @RequestHeader("Authorization") String authorizationHeader) {

        try {
            Customer customer = customerService.getCustomerFromAuth(authorizationHeader);
            if (!Objects.equals(customerId, customer.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Product> productList = cartService.getCartItems(customerId);
            if (productList == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(productList);
        } catch (ApiException e) {
            log.error("Error occurred while getting cart items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}