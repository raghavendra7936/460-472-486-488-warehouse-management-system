package com.warehouse.service.impl;

import com.warehouse.model.Inventory;
import com.warehouse.repository.InventoryRepository;
import com.warehouse.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Inventory createInventory(Inventory inventory) {
        updateInventoryStatus(inventory);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateInventory(Long id, Inventory inventory) {
        Inventory existingInventory = getInventoryById(id);
        existingInventory.setName(inventory.getName());
        existingInventory.setQuantity(inventory.getQuantity());
        existingInventory.setUnitPrice(inventory.getUnitPrice());
        existingInventory.setVolume(inventory.getVolume());
        updateInventoryStatus(existingInventory);
        return inventoryRepository.save(existingInventory);
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findByQuantityLessThan(10);
    }

    @Override
    public Inventory updateStock(Long id, Integer quantity) {
        Inventory inventory = getInventoryById(id);
        inventory.setQuantity(quantity);
        updateInventoryStatus(inventory);
        return inventoryRepository.save(inventory);
    }

    private void updateInventoryStatus(Inventory inventory) {
        if (inventory.getQuantity() <= 0) {
            inventory.setStatus("OUT_OF_STOCK");
        } else if (inventory.getQuantity() < 10) {
            inventory.setStatus("LOW_STOCK");
        } else {
            inventory.setStatus("AVAILABLE");
        }
    }
} 