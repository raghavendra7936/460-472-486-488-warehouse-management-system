package com.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventory")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orderItems"})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Double volume = 0.0; // in cubic meters

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id")
    @JsonIgnoreProperties("inventoryItems")
    private WarehouseSection section;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, LOW_STOCK, OUT_OF_STOCK

    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getVolume() {
        return volume != null ? volume : 0.0;
    }

    public void setVolume(Double volume) {
        this.volume = volume != null ? volume : 0.0;
    }

    public WarehouseSection getSection() {
        return section;
    }

    public void setSection(WarehouseSection section) {
        this.section = section;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 