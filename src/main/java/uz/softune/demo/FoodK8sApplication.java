package uz.softune.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uz.softune.demo.metrics.MetricsService;

import java.util.Random;

@SpringBootApplication
public class FoodK8sApplication {

    private final MetricsService metricsService;

    public FoodK8sApplication(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    public static void main(String[] args) {
		SpringApplication.run(FoodK8sApplication.class, args);
	}

    @PostConstruct
    public void init() {
        for(int i = 0; i < new Random(100L).nextInt(); i++){
            metricsService.incrementLoginFailure();
        }

        for(int i = 0; i < new Random(60L).nextInt(); i++){
            metricsService.incrementFarmerPayout();
        }
    }
}
