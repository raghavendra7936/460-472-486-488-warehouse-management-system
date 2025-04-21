package com.warehouse.controller;

import com.warehouse.model.Order;
import com.warehouse.model.Customer;
import com.warehouse.model.Inventory;
import com.warehouse.model.OrderItem;
import com.warehouse.repository.OrderRepository;
import com.warehouse.repository.CustomerRepository;
import com.warehouse.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<Inventory> inventoryItems = inventoryRepository.findAll();
        logger.info("Found {} inventory items", inventoryItems.size());
        
        // Fetch orders with their relationships
        List<Order> orders = orderRepository.findAll();
        orders.forEach(order -> {
            // Initialize lazy-loaded relationships
            order.getCustomer().getName(); // Force customer loading
            order.getOrderItems().forEach(item -> {
                item.getInventory().getName(); // Force inventory loading
            });
        });
        
        model.addAttribute("orders", orders);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("inventoryItems", inventoryItems);
        return "orders/list";
    }

    @GetMapping("/inventory")
    @ResponseBody
    public ResponseEntity<List<Inventory>> getInventory() {
        try {
            List<Inventory> items = inventoryRepository.findAll();
            logger.info("Successfully retrieved {} inventory items", items.size());
            return ResponseEntity.ok().body(items);
        } catch (Exception e) {
            logger.error("Error retrieving inventory items", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @ResponseBody
    @Transactional
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            logger.info("Received order creation request: {}", order);
            
            // Validate order data
            if (order.getCustomer() == null || order.getCustomer().getId() == null) {
                logger.error("Order is missing customer information");
                return ResponseEntity.badRequest().body("Customer information is required");
            }

            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                logger.error("Order has no items");
                return ResponseEntity.badRequest().body("Order must contain at least one item");
            }

            // Load and validate customer
            Customer customer = customerRepository.findById(order.getCustomer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            order.setCustomer(customer);

            // Process order items
            double totalAmount = 0.0;
            for (OrderItem item : order.getOrderItems()) {
                // Load and validate inventory item
                Inventory inventory = inventoryRepository.findById(item.getInventory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory item not found: " + item.getInventory().getId()));
                
                // Validate quantity
                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than 0");
                }
                if (item.getQuantity() > inventory.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient quantity for item: " + inventory.getName());
                }

                // Set inventory reference
                item.setInventory(inventory);
                item.setOrder(order);
                
                // Calculate and set prices
                item.setUnitPrice(inventory.getUnitPrice());
                item.setTotalPrice(item.getUnitPrice() * item.getQuantity());
                totalAmount += item.getTotalPrice();
            }

            // Set order properties
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setTotalAmount(totalAmount);
            
            if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
                order.setOrderNumber("ORD-" + System.currentTimeMillis());
            }

            // Log order details
            logger.info("Creating order: number={}, customer={}, items={}, total={}", 
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getOrderItems().size(),
                order.getTotalAmount());

            // Save the order
            Order savedOrder = orderRepository.save(order);
            logger.info("Order created successfully with ID: {}", savedOrder.getId());
            
            return ResponseEntity.ok(savedOrder);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating order", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating order", e);
            return ResponseEntity.internalServerError()
                .body("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping("/customers")
    @ResponseBody
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{orderId}/items")
    @ResponseBody
    public ResponseEntity<?> getOrderItems(@PathVariable Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            List<OrderItem> items = order.getOrderItems();
            logger.info("Retrieved {} items for order {}", items.size(), orderId);
            
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException e) {
            logger.error("Order not found", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving order items", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{orderId}")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody Order orderData) {
        try {
            logger.info("Received order update request for order ID: {}", orderId);
            
            // Find the existing order
            Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            // Update customer if changed
            if (orderData.getCustomer() != null && orderData.getCustomer().getId() != null) {
                Customer customer = customerRepository.findById(orderData.getCustomer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
                existingOrder.setCustomer(customer);
            }
            
            // Update status if changed
            if (orderData.getStatus() != null) {
                existingOrder.setStatus(orderData.getStatus());
            }
            
            // Update order items if provided
            if (orderData.getOrderItems() != null && !orderData.getOrderItems().isEmpty()) {
                double totalAmount = 0.0;
                
                // Create a map of existing items by inventory ID for quick lookup
                Map<Long, OrderItem> existingItemsMap = existingOrder.getOrderItems().stream()
                    .collect(Collectors.toMap(item -> item.getInventory().getId(), item -> item));
                
                // Process each item in the request
                for (OrderItem itemData : orderData.getOrderItems()) {
                    // Load and validate inventory item
                    Inventory inventory = inventoryRepository.findById(itemData.getInventory().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Inventory item not found: " + itemData.getInventory().getId()));
                    
                    // Validate quantity
                    if (itemData.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Quantity must be greater than 0");
                    }
                    if (itemData.getQuantity() > inventory.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient quantity for item: " + inventory.getName());
                    }
                    
                    OrderItem orderItem;
                    // Check if this item already exists in the order
                    if (existingItemsMap.containsKey(inventory.getId())) {
                        // Update existing item
                        orderItem = existingItemsMap.get(inventory.getId());
                        orderItem.setQuantity(itemData.getQuantity());
                        orderItem.setUnitPrice(inventory.getUnitPrice());
                        orderItem.setTotalPrice(orderItem.getUnitPrice() * orderItem.getQuantity());
                        // Remove from map to track which items were processed
                        existingItemsMap.remove(inventory.getId());
                    } else {
                        // Create new order item
                        orderItem = new OrderItem();
                        orderItem.setOrder(existingOrder);
                        orderItem.setInventory(inventory);
                        orderItem.setQuantity(itemData.getQuantity());
                        orderItem.setUnitPrice(inventory.getUnitPrice());
                        orderItem.setTotalPrice(orderItem.getUnitPrice() * orderItem.getQuantity());
                        existingOrder.getOrderItems().add(orderItem);
                    }
                    
                    totalAmount += orderItem.getTotalPrice();
                }
                
                // Remove any items that weren't in the update request
                existingOrder.getOrderItems().removeIf(item -> existingItemsMap.containsKey(item.getInventory().getId()));
                
                // Update total amount
                existingOrder.setTotalAmount(totalAmount);
            }
            
            // Save the updated order
            Order updatedOrder = orderRepository.save(existingOrder);
            logger.info("Order updated successfully with ID: {}", updatedOrder.getId());
            
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating order", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating order", e);
            return ResponseEntity.internalServerError()
                .body("Error updating order: " + e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            logger.info("Received order cancellation request for order ID: {}", orderId);
            
            // Find the existing order
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            // Check if order can be cancelled
            if ("COMPLETED".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Cannot cancel a completed order");
            }
            if ("CANCELLED".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Order is already cancelled");
            }
            
            // Update order status
            order.setStatus("CANCELLED");
            
            // Save the updated order
            Order cancelledOrder = orderRepository.save(order);
            logger.info("Order cancelled successfully with ID: {}", cancelledOrder.getId());
            
            return ResponseEntity.ok(cancelledOrder);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error cancelling order", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error cancelling order", e);
            return ResponseEntity.internalServerError()
                .body("Error cancelling order: " + e.getMessage());
        }
    }
} 