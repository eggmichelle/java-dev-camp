package za.co.entelect.services.pc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.pc.CartEntity;
import za.co.entelect.mappers.pc.CartMapper;
import za.co.entelect.pojo.pc.Cart;
import za.co.entelect.pojo.pc.CartItem;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.repository.pc.CartRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CartService {

    @Autowired
    private final CartRepository cartRepository;

    @Autowired
    private final CartMapper cartMapper;

    @Autowired
    CartItemService cartItemService;

    @Autowired
    ProductService productService;

    public Cart getCartByCustomerId(Long customerId) {
        CartEntity cartEntity = cartRepository.findByCustomerId(customerId);

        if (cartEntity != null) {
            return cartMapper.toDomain(cartEntity);
        }
        return null;
    }

    public Cart createCart(Long customerId) {
        Cart cart = new Cart(
                null,
                customerId,
                new ArrayList<>()
        );

        CartEntity savedCart = cartRepository.save(cartMapper.toEntity(cart));

        return cartMapper.toDomain(savedCart);
    }

    public Cart getCart(Long cartId) {
        CartEntity cartEntity = cartRepository.findById(cartId).orElse(null);
        if (cartEntity == null) {
            log.warn("Cart with ID {} not found", cartId);
            return null;
        }
        return cartMapper.toDomain(cartEntity);
    }

    public void deleteCart(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            cartItemService.deleteCartItem(cartItem, cart);
        }
        cartRepository.delete(cartMapper.toEntity(cart));
    }

    public List<Product> getCartItems(Long customerId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) { return null; }
        List<CartItem> cartItemList = cartItemService.getAllCartItems(cart.getCartId());

        List<Product> productList = new ArrayList<>();

        for (CartItem cartItem : cartItemList) {
            productList.add(cartItem.getProduct());
        }

        return productList;
    }

    public List<Product> getProducts(Cart cart) {
        List<Product> products = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            products.add(cartItem.getProduct());
        }
        return products;
    }

}
