package com.warehouse.controller;

import com.warehouse.model.Inventory;
import com.warehouse.model.WarehouseSection;
import com.warehouse.service.InventoryService;
import com.warehouse.repository.WarehouseSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private WarehouseSectionRepository warehouseSectionRepository;

    @GetMapping
    public String listInventory(Model model) {
        model.addAttribute("inventoryItems", inventoryService.getAllInventory());
        model.addAttribute("sections", warehouseSectionRepository.findAll());
        return "inventory/list";
    }

    @PostMapping
    public String createInventory(Inventory inventory) {
        inventoryService.createInventory(inventory);
        return "redirect:/inventory";
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/api")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Inventory> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(id, quantity));
    }
} 