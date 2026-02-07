package uz.softune.demo.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // Counters
    private final Counter farmProductUpdates;
    private final Counter farmProductAdditions;
    private final Counter farmProductRemovals;
    private final Counter farmProductUpdateFailures;

    // Gauges
    private final Map<String, Integer> productQuantities = new ConcurrentHashMap<>();
    private final Gauge totalProducts;

    // Timers
    private final Timer productUpdateTimer;
    private final Timer productAdditionTimer;


    // ================= ORDER METRICS =================

    // Counters
    private final Counter ordersCreated;
    private final Counter ordersConfirmed;
    private final Counter ordersFailed;

    // Timers
    private final Timer orderProcessingTimer;

    // Gauges
    private final Map<Long, Integer> activeOrders = new ConcurrentHashMap<>();
    private final Gauge activeOrdersGauge;


    // ================= NOTIFICATION METRICS =================

    private final Counter notificationsSent;
    private final Counter notificationsFailed;
    private final Counter notificationRetries;

    private final Timer notificationLatencyTimer;


    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.farmProductUpdates = Counter.builder("foodapp_farm_product_updates_total")
                .description("Total number of farm product updates")
                .register(meterRegistry);

        this.farmProductAdditions = Counter.builder("foodapp_farm_product_additions_total")
                .description("Total number of new farm products added")
                .register(meterRegistry);

        this.farmProductRemovals = Counter.builder("foodapp_farm_product_removals_total")
                .description("Total number of farm products removed")
                .register(meterRegistry);

        this.farmProductUpdateFailures = Counter.builder("foodapp_farm_product_update_failures_total")
                .description("Total failed product updates")
                .register(meterRegistry);

        this.totalProducts = Gauge.builder("foodapp_total_products", productQuantities, Map::size)
                .description("Current total number of products")
                .register(meterRegistry);

        this.productUpdateTimer = Timer.builder("foodapp_product_update_duration_seconds")
                .description("Time taken to update a product")
                .register(meterRegistry);

        this.productAdditionTimer = Timer.builder("foodapp_product_addition_duration_seconds")
                .description("Time taken to add a new product")
                .register(meterRegistry);


        this.ordersCreated = Counter.builder("foodapp_orders_created_total")
                .description("Total number of orders created")
                .register(meterRegistry);

        this.ordersConfirmed = Counter.builder("foodapp_orders_confirmed_total")
                .description("Total number of orders confirmed")
                .register(meterRegistry);

        this.ordersFailed = Counter.builder("foodapp_orders_failed_total")
                .description("Total number of orders failed")
                .register(meterRegistry);

        this.orderProcessingTimer = Timer.builder("foodapp_order_processing_duration_seconds")
                .description("Time taken to process orders")
                .register(meterRegistry);

        this.activeOrdersGauge = Gauge.builder(
                        "foodapp_active_orders",
                        activeOrders,
                        Map::size
                ).description("Number of active orders in system")
                .register(meterRegistry);


        this.notificationsSent = Counter.builder("foodapp_notifications_sent_total")
                .description("Total number of notifications sent successfully")
                .register(meterRegistry);

        this.notificationsFailed = Counter.builder("foodapp_notifications_failed_total")
                .description("Total number of failed notifications")
                .register(meterRegistry);

        this.notificationRetries = Counter.builder("foodapp_notification_retries_total")
                .description("Total number of notification retries")
                .register(meterRegistry);

        this.notificationLatencyTimer = Timer.builder("foodapp_notification_latency_seconds")
                .description("Time taken to send order notifications")
                .register(meterRegistry);


    }

    // Counter increments
    public void incrementUpdate() {
        farmProductUpdates.increment();
    }

    public void incrementAdd() {
        farmProductAdditions.increment();
    }

    public void incrementRemove() {
        farmProductRemovals.increment();
    }

    public void incrementUpdateFailure() {
        farmProductUpdateFailures.increment();
    }

    // Timer recording
    public void recordUpdateTime(long durationMs) {
        productUpdateTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void recordAdditionTime(long durationMs) {
        productAdditionTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // Gauge updates
    public void setProductQuantity(String productName, int quantity) {
        productQuantities.put(productName, quantity);
    }

    public void removeProduct(String productName) {
        productQuantities.remove(productName);
    }
    // -------- Counters --------
    public void incrementOrdersCreated() {
        ordersCreated.increment();
    }

    public void incrementOrdersConfirmed() {
        ordersConfirmed.increment();
    }

    public void incrementOrdersFailed() {
        ordersFailed.increment();
    }

    // -------- Timers --------
    public void recordOrderProcessingTime(long durationMs) {
        orderProcessingTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // -------- Gauges --------
    public void addActiveOrder(Long orderId) {
        activeOrders.put(orderId, 1);
    }

    public void removeActiveOrder(Long orderId) {
        activeOrders.remove(orderId);
    }

    public void incrementNotificationSent() {
        notificationsSent.increment();
    }

    public void incrementNotificationFailed() {
        notificationsFailed.increment();
    }

    public void incrementNotificationRetry() {
        notificationRetries.increment();
    }

    public void recordNotificationLatency(long durationMs) {
        notificationLatencyTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

}

