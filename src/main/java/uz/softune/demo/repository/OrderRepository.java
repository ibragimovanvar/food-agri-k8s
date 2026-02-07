package uz.softune.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.softune.demo.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByFarmerId(String farmerId);

    List<Order> findByBuyerId(String buyerId);
}