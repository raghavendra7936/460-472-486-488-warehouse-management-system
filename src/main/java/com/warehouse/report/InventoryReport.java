package com.warehouse.report;

import com.warehouse.model.Inventory;
import com.warehouse.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryReport implements Report {
    
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public String generate() {
        List<Inventory> inventory = inventoryRepository.findAll();
        StringBuilder report = new StringBuilder();
        report.append("Inventory Report\n");
        report.append("================\n\n");
        
        for (Inventory item : inventory) {
            report.append(String.format("Item: %s\n", item.getName()));
            report.append(String.format("Quantity: %d\n", item.getQuantity()));
            report.append(String.format("Status: %s\n", item.getStatus()));
            report.append("----------------\n");
        }
        
        return report.toString();
    }

    @Override
    public String getType() {
        return "INVENTORY";
    }
} 