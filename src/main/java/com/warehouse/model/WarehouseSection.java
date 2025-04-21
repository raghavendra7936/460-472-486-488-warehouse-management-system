package com.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouse_sections")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "inventoryItems"})
public class WarehouseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Code is required")
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than 0")
    @Column(nullable = false)
    private Double capacity; // in cubic meters

    @NotNull(message = "Used space is required")
    @Column(nullable = false)
    private Double usedSpace = 0.0; // in cubic meters

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, FULL, MAINTENANCE

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventoryItems = new ArrayList<>();

    public Double getAvailableSpace() {
        return capacity - usedSpace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateUsedSpace(Double space) {
        if (space == null) {
            throw new IllegalArgumentException("Space cannot be null");
        }
        
        this.usedSpace += space;
        updateStatus();
    }

    private void updateStatus() {
        if (this.usedSpace >= this.capacity) {
            this.status = "FULL";
        } else if (this.usedSpace < 0) {
            this.usedSpace = 0.0;
            this.status = "AVAILABLE";
        } else {
            this.status = "AVAILABLE";
        }
    }

    // Helper method to add inventory item
    public void addInventoryItem(Inventory item) {
        if (item != null) {
            inventoryItems.add(item);
            item.setSection(this);
            updateUsedSpace(item.getVolume());
        }
    }

    // Helper method to remove inventory item
    public void removeInventoryItem(Inventory item) {
        if (item != null && inventoryItems.remove(item)) {
            item.setSection(null);
            updateUsedSpace(-item.getVolume());
        }
    }
} 