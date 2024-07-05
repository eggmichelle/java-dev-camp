package za.co.entelect.services.pc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.pc.CartItemEntity;
import za.co.entelect.mappers.pc.CartItemMapper;
import za.co.entelect.pojo.pc.Cart;
import za.co.entelect.pojo.pc.CartItem;
import za.co.entelect.repository.pc.CartItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CartItemService {

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final CartItemMapper cartItemMapper;

    @Autowired
    private final ProductService productService;

    public void createCartItem(Cart cart, Long productId) {
        CartItem cartItem = new CartItem(
                null,
                productService.getProductById(productId)
        );

        CartItemEntity cartItemEntity = cartItemRepository.save(cartItemMapper.toEntity(cartItem, cart));

        cartItemMapper.toDomain(cartItemEntity);
    }

    public void deleteCartItem(CartItem cartItem, Cart cart) {
        cartItemRepository.delete(cartItemMapper.toEntity(cartItem, cart));
    }

    public List<CartItem> getAllCartItems(Long cartId) {
        List<CartItemEntity> cartItemEntities = cartItemRepository.findAllByCartId(cartId);

        return cartItemEntities.stream().map(cartItemMapper::toDomain).collect(Collectors.toList());
    }

}
