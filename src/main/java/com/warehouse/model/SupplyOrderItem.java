package com.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "supply_order_items")
@Data
public class SupplyOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supply_order_id", nullable = false)
    private SupplyOrder supplyOrder;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Double totalPrice;
} 