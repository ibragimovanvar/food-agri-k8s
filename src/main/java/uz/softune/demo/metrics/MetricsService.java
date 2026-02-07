package uz.softune.demo.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // ================= FARM PRODUCT METRICS =================
    private final Counter farmProductUpdates;
    private final Counter farmProductAdditions;
    private final Counter farmProductRemovals;
    private final Counter farmProductUpdateFailures;

    // NEW: Spoilage & Low Stock (O'ZIMIZ QO'SHGAN)
    private final Counter productSpoilage;
    private final Counter lowStockAlerts;

    private final Map<String, Integer> productQuantities = new ConcurrentHashMap<>();
    private final Gauge totalProducts;

    private final Timer productUpdateTimer;
    private final Timer productAdditionTimer;


    // ================= ORDER & DELIVERY METRICS =================
    private final Counter ordersCreated;
    private final Counter ordersConfirmed;
    private final Counter ordersFailed;

    // NEW: Delivery & Discounts (O'ZIMIZ QO'SHGAN)
    private final Counter deliveryDispatched;
    private final Counter discountsApplied;

    private final Timer orderProcessingTimer;

    private final Map<Long, Integer> activeOrders = new ConcurrentHashMap<>();
    private final Gauge activeOrdersGauge;


    // ================= NOTIFICATION & USER METRICS =================
    private final Counter notificationsSent;
    private final Counter notificationsFailed;
    private final Counter notificationRetries;

    // NEW: User & Search Activity (O'ZIMIZ QO'SHGAN)
    private final Counter searchQueries;
    private final Counter userLoginFailures;
    private final Counter farmerPayouts;

    private final Timer notificationLatencyTimer;


    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // --- Original Farm Metrics ---
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

        // --- NEW: Spoilage & Stock ---
        this.productSpoilage = Counter.builder("foodapp_product_spoilage_total")
                .description("Total items reported as spoiled or wasted")
                .tag("reason", "expired")
                .register(meterRegistry);

        this.lowStockAlerts = Counter.builder("foodapp_inventory_low_stock_alerts_total")
                .description("Alerts triggered when product quantity is critically low")
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


        // --- Original Order Metrics ---
        this.ordersCreated = Counter.builder("foodapp_orders_created_total")
                .description("Total number of orders created")
                .register(meterRegistry);

        this.ordersConfirmed = Counter.builder("foodapp_orders_confirmed_total")
                .description("Total number of orders confirmed")
                .register(meterRegistry);

        this.ordersFailed = Counter.builder("foodapp_orders_failed_total")
                .description("Total number of orders failed")
                .register(meterRegistry);

        // --- NEW: Delivery & Discount ---
        this.deliveryDispatched = Counter.builder("foodapp_delivery_dispatched_total")
                .description("Total orders dispatched for delivery")
                .register(meterRegistry);

        this.discountsApplied = Counter.builder("foodapp_discounts_applied_total")
                .description("Total number of promo codes or discounts used")
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


        // --- Notification & User Metrics ---
        this.notificationsSent = Counter.builder("foodapp_notifications_sent_total")
                .description("Total number of notifications sent successfully")
                .register(meterRegistry);

        this.notificationsFailed = Counter.builder("foodapp_notifications_failed_total")
                .description("Total number of failed notifications")
                .register(meterRegistry);

        this.notificationRetries = Counter.builder("foodapp_notification_retries_total")
                .description("Total number of notification retries")
                .register(meterRegistry);

        // --- NEW: User Experience ---
        this.searchQueries = Counter.builder("foodapp_search_queries_total")
                .description("Total product search queries performed by users")
                .register(meterRegistry);

        this.userLoginFailures = Counter.builder("foodapp_user_login_failures_total")
                .description("Total failed login attempts (security tracking)")
                .register(meterRegistry);

        this.farmerPayouts = Counter.builder("foodapp_farmer_payouts_total")
                .description("Total number of payouts processed to farmers")
                .register(meterRegistry);


        this.notificationLatencyTimer = Timer.builder("foodapp_notification_latency_seconds")
                .description("Time taken to send order notifications")
                .register(meterRegistry);
    }

    // ================= METHODS =================

    public void incrementUpdate() { farmProductUpdates.increment(); }
    public void incrementAdd() { farmProductAdditions.increment(); }
    public void incrementRemove() { farmProductRemovals.increment(); }
    public void incrementUpdateFailure() { farmProductUpdateFailures.increment(); }

    // --- NEW METHODS ---
    public void incrementSpoilage() { productSpoilage.increment(); }
    public void incrementLowStockAlert() { lowStockAlerts.increment(); }
    public void incrementDeliveryDispatched() { deliveryDispatched.increment(); }
    public void incrementDiscountApplied() { discountsApplied.increment(); }
    public void incrementSearchQuery() { searchQueries.increment(); }
    public void incrementLoginFailure() { userLoginFailures.increment(); }
    public void incrementFarmerPayout() { farmerPayouts.increment(); }

    // Timers
    public void recordUpdateTime(long durationMs) {
        productUpdateTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
    public void recordAdditionTime(long durationMs) {
        productAdditionTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    // Gauges
    public void setProductQuantity(String productName, int quantity) {
        productQuantities.put(productName, quantity);
        // Logic: agar mahsulot 10 dan kam qolsa, alert chalinsin
        if (quantity < 10) {
            incrementLowStockAlert();
        }
    }
    public void removeProduct(String productName) {
        productQuantities.remove(productName);
    }

    // Order Counters
    public void incrementOrdersCreated() { ordersCreated.increment(); }
    public void incrementOrdersConfirmed() {
        ordersConfirmed.increment();
        // Har 3 ta orderdan bittasida chegirma ishlatilgan deb simulyatsiya qilamiz
        if (Math.random() > 0.7) {
            incrementDiscountApplied();
        }
        // Order tasdiqlangach, deliveryga chiqadi
        incrementDeliveryDispatched();
    }
    public void incrementOrdersFailed() { ordersFailed.increment(); }

    // Timers
    public void recordOrderProcessingTime(long durationMs) {
        orderProcessingTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    // Active Orders Gauge
    public void addActiveOrder(Long orderId) { activeOrders.put(orderId, 1); }
    public void removeActiveOrder(Long orderId) { activeOrders.remove(orderId); }

    // Notifications
    public void incrementNotificationSent() { notificationsSent.increment(); }
    public void incrementNotificationFailed() { notificationsFailed.increment(); }
    public void incrementNotificationRetry() { notificationRetries.increment(); }
    public void recordNotificationLatency(long durationMs) {
        notificationLatencyTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
}