package uz.softune.demo.service;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.softune.demo.entity.Order;
import uz.softune.demo.entity.OrderStatus;
import uz.softune.demo.metrics.MetricsService;
import uz.softune.demo.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository repository;
    private final MetricsService metrics;
    private final NotificationService notificationService;


    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public List<Order> getOrdersByFarmer(String farmerId) {
        return repository.findByFarmerId(farmerId);
    }

    @Transactional
    public Order placeOrder(Order order) {

        long start = System.currentTimeMillis();

        try {
            order.setStatus(OrderStatus.CREATED);
            order.setCreatedAt(LocalDateTime.now());

            Order saved = repository.save(order);

            metrics.incrementOrdersCreated();
            metrics.addActiveOrder(saved.getId());

            log.info(
                    "Food-APP: INFO: Order placed [orderId={}, farmerId={}, buyerId={}]",
                    saved.getId(), saved.getFarmerId(), saved.getBuyerId()
            );

            // Simulate successful processing (notification comes later)
            saved.setStatus(OrderStatus.CONFIRMED);
            repository.save(saved);

            metrics.incrementOrdersConfirmed();

            notificationService.notifyFarmer(saved.getId(), saved.getFarmerId());
            return saved;

        } catch (Exception e) {

            metrics.incrementOrdersFailed();

            log.error(
                    "Food-APP: ERROR: Order processing failed: {}",
                    e.getMessage(), e
            );
            throw e;

        } finally {
            metrics.recordOrderProcessingTime(System.currentTimeMillis() - start);
            if (order.getId() != null) {
                metrics.removeActiveOrder(order.getId());
            }
        }
    }

}