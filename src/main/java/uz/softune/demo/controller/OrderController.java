package uz.softune.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.softune.demo.entity.Order;
import uz.softune.demo.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<Order>> getByFarmer(@PathVariable String farmerId) {
        return ResponseEntity.ok(service.getOrdersByFarmer(farmerId));
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(service.placeOrder(order));
    }
}