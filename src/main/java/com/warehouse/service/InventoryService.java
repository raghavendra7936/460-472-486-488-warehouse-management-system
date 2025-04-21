package com.warehouse.service;

import com.warehouse.model.Inventory;
import java.util.List;

public interface InventoryService {
    Inventory createInventory(Inventory inventory);
    Inventory updateInventory(Long id, Inventory inventory);
    void deleteInventory(Long id);
    Inventory getInventoryById(Long id);
    List<Inventory> getAllInventory();
    List<Inventory> getLowStockItems();
    Inventory updateStock(Long id, Integer quantity);
} 