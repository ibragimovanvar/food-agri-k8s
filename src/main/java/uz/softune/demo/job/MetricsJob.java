package uz.softune.demo.job;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.softune.demo.entity.Product;
import uz.softune.demo.service.ProductService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MetricsJob {

    private final ProductService productService;
    private static final Logger log = LoggerFactory.getLogger(MetricsJob.class);

    @Scheduled(fixedRate = 20000) // every 20 seconds
    public void generateMetrics() {
        try {
            // 1️⃣ Simulate product addition
            Product newProduct = Product.builder()
                    .name("Tomatoes")
                    .category("Vegetable")
                    .price(1.5)
                    .quantity(100)
                    .farmerId("farmer1")
                    .build();
            productService.addProduct(newProduct);

            // 2️⃣ Simulate product update
            List<Product> products = productService.getAllProducts();
            if (!products.isEmpty()) {
                Product first = products.get(0);
                first.setQuantity(first.getQuantity() + 10); // increment quantity
                productService.updateProduct(first.getId(), first);
            }

            // 3️⃣ Optionally remove a product for demo purposes (simulate removals)
            if (products.size() > 1) {
                Product toRemove = products.get(1);
                productService.removeProduct(toRemove.getId());
            }

            log.info("Food-APP: INFO: MetricsJob executed successfully.");

        } catch (Exception e) {
            log.error("Food-APP: ERROR: MetricsJob failed: {}", e.getMessage());
        }
    }
}
