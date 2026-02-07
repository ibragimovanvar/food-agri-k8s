package uz.softune.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.softune.demo.metrics.MetricsService;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final MetricsService metrics;
    private final Random random = new Random();

    public void notifyFarmer(Long orderId, String farmerId) {

        long start = System.currentTimeMillis();

        try {
            // Simulate network delay
            Thread.sleep(100 + random.nextInt(300));

            // Simulate 80% success rate
            if (random.nextInt(10) < 8) {
                metrics.incrementNotificationSent();
                log.info(
                        "Food-APP: INFO: Notification sent [orderId={}, farmerId={}]",
                        orderId, farmerId
                );
            } else {
                throw new RuntimeException("Simulated notification failure");
            }

        } catch (Exception e) {
            metrics.incrementNotificationFailed();
            log.warn(
                    "Food-APP: WARN: Notification failed [orderId={}, farmerId={}]",
                    orderId, farmerId
            );

            retryNotification(orderId, farmerId);

        } finally {
            metrics.recordNotificationLatency(System.currentTimeMillis() - start);
        }
    }

    private void retryNotification(Long orderId, String farmerId) {
        metrics.incrementNotificationRetry();
        log.info(
                "Food-APP: INFO: Retrying notification [orderId={}, farmerId={}]",
                orderId, farmerId
        );
    }
}