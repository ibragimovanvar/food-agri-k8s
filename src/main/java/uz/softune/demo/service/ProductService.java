package uz.softune.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.softune.demo.entity.Product;
import uz.softune.demo.metrics.MetricsService;
import uz.softune.demo.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final MetricsService metrics;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public List<Product> getProductsByFarmer(String farmerId) {
        metrics.incrementSearchQuery();
        return repository.findByFarmerId(farmerId);
    }

    @Transactional
    public Product addProduct(Product product) {
        long start = System.currentTimeMillis();
        Product saved = repository.save(product);

        metrics.incrementAdd();
        metrics.recordAdditionTime(System.currentTimeMillis() - start);
        metrics.setProductQuantity(product.getName(), product.getQuantity());

        log.info("Food-APP: INFO: Added product {} with quantity {}", product.getName(), product.getQuantity());
        return saved;
    }

    @Transactional
    public Product updateProduct(Long id, Product updated) {
        long start = System.currentTimeMillis();

        try {
            Optional<Product> existingOpt = repository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new RuntimeException("Product not found with id " + id);
            }
            Product existing = existingOpt.get();
            existing.setName(updated.getName());
            existing.setCategory(updated.getCategory());
            existing.setPrice(updated.getPrice());
            existing.setQuantity(updated.getQuantity());

            Product saved = repository.save(existing);

            metrics.incrementUpdate();
            metrics.recordUpdateTime(System.currentTimeMillis() - start);
            metrics.setProductQuantity(saved.getName(), saved.getQuantity());

            log.info("Food-APP: INFO: Updated product {} to quantity {}", saved.getName(), saved.getQuantity());
            return saved;

        } catch (Exception e) {
            metrics.incrementUpdateFailure();
            log.error("Food-APP: ERROR: Failed to update product id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void removeProduct(Long id) {
        repository.findById(id).ifPresent(product -> {
            repository.delete(product);
            metrics.incrementSpoilage();
            metrics.incrementRemove();
            metrics.removeProduct(product.getName());
            log.info("Food-APP: INFO: Removed product {}", product.getName());
        });
    }
}