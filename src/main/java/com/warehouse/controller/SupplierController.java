package com.warehouse.controller;

import com.warehouse.model.Supplier;
import com.warehouse.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "suppliers/list";
    }

    
    @PostMapping
    public String createSupplier(Supplier supplier) {
        supplierRepository.save(supplier);
        return "redirect:/suppliers";
    }
    

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Supplier> getSupplier(@PathVariable Long id) {
        return ResponseEntity.ok(supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id)));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplier.setId(id);
        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 