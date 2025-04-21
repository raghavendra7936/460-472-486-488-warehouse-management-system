package com.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "supply_orders")
@Data
public class SupplyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "supplyOrder", cascade = CascadeType.ALL)
    private List<SupplyOrderItem> items;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String status; // PENDING, RECEIVED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column
    private LocalDateTime expectedDeliveryDate;

    @Column
    private LocalDateTime actualDeliveryDate;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
} 