package za.co.entelect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.services.pc.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void testGetProductById_Success() throws Exception {
        Product sampleProduct = new Product();
        sampleProduct.setProductId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setDescription("Test Description");
        sampleProduct.setPrice(BigDecimal.valueOf(50.00));
        sampleProduct.setImageUrl("test-url");
        sampleProduct.setQualifyingCustomerTypeIds(List.of(1L, 2L));
        sampleProduct.setQualifyingAccountTypeIds(List.of(3L, 4L));

        when(productService.getProductById(anyLong())).thenReturn(sampleProduct);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(sampleProduct.getProductId()))
                .andExpect(jsonPath("$.name").value(sampleProduct.getName()))
                .andExpect(jsonPath("$.description").value(sampleProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(sampleProduct.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(sampleProduct.getImageUrl()));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testGetAllProducts_Success() throws Exception {
        List<Product> sampleProducts = new ArrayList<>();

        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description for Product 1");
        product1.setPrice(BigDecimal.valueOf(50.00));
        product1.setImageUrl("image-url-1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description for Product 2");
        product2.setPrice(BigDecimal.valueOf(75.00));
        product2.setImageUrl("image-url-2");

        Product product3 = new Product();
        product2.setProductId(3L);
        product2.setName("Product 3");
        product2.setDescription("Description for Product 3");
        product2.setPrice(BigDecimal.valueOf(100.00));
        product2.setImageUrl("image-url-3");

        sampleProducts.add(product1);
        sampleProducts.add(product2);
        sampleProducts.add(product3);

        when(productService.getAllProducts()).thenReturn(sampleProducts);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0].productId").value(product1.getProductId()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[0].description").value(product1.getDescription()))
                .andExpect(jsonPath("$[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$[0].imageUrl").value(product1.getImageUrl()))

                .andExpect(jsonPath("$[1].productId").value(product2.getProductId()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()))
                .andExpect(jsonPath("$[1].description").value(product2.getDescription()))
                .andExpect(jsonPath("$[1].price").value(product2.getPrice()))
                .andExpect(jsonPath("$[1].imageUrl").value(product2.getImageUrl()))

                .andExpect(jsonPath("$[2].productId").value(product3.getProductId()))
                .andExpect(jsonPath("$[2].name").value(product3.getName()))
                .andExpect(jsonPath("$[2].description").value(product3.getDescription()))
                .andExpect(jsonPath("$[2].price").value(product3.getPrice()))
                .andExpect(jsonPath("$[2].imageUrl").value(product3.getImageUrl()));
    }

    @Test
    void testGetAllProducts_noContent() throws Exception {
        when(productService.getAllProducts()).thenThrow(NoResultException.class);
        mockMvc.perform(get("/products"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetProductsInPages_Success() throws Exception {
        int page = 0;
        int size = 10;
        List<Product> paginatedProducts = new ArrayList<>();

        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description for Product 1");
        product1.setPrice(BigDecimal.valueOf(50.00));
        product1.setImageUrl("image-url-1");

        paginatedProducts.add(product1);

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description for Product 2");
        product2.setPrice(BigDecimal.valueOf(75.00));
        product2.setImageUrl("image-url-2");

        paginatedProducts.add(product2);

        when(productService.getPaginatedProducts(page, size)).thenReturn(paginatedProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/paginated")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(paginatedProducts.size()));
    }

    @Test
    public void testGetProductsInPages_EmptyList() throws Exception {
        int page = 0;
        int size = 10;
        List<Product> emptyList = Collections.emptyList();
        when(productService.getPaginatedProducts(page, size)).thenReturn(emptyList);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/paginated")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}