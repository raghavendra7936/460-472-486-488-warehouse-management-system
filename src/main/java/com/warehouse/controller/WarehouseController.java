package com.warehouse.controller;

import com.warehouse.model.WarehouseSection;
import com.warehouse.repository.WarehouseSectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);

    @Autowired
    private WarehouseSectionRepository warehouseSectionRepository;

    @GetMapping
    public String listSections(Model model) {
        try {
            logger.debug("Fetching all warehouse sections");
            model.addAttribute("sections", warehouseSectionRepository.findAll());
            return "warehouse/list";
        } catch (Exception e) {
            logger.error("Error fetching warehouse sections", e);
            model.addAttribute("errorMessage", "Failed to load warehouse sections: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public ResponseEntity<WarehouseSection> createSectionJson(@RequestBody WarehouseSection section) {
        try {
            logger.debug("Creating new warehouse section: {}", section);
            section.setUsedSpace(0.0);
            section.setStatus("AVAILABLE");
            WarehouseSection savedSection = warehouseSectionRepository.save(section);
            logger.info("Successfully created warehouse section with ID: {}", savedSection.getId());
            return ResponseEntity.ok(savedSection);
        } catch (Exception e) {
            logger.error("Error creating warehouse section", e);
            throw new RuntimeException("Failed to create warehouse section: " + e.getMessage());
        }
    }

    @PostMapping(consumes = "application/x-www-form-urlencoded")
    public String createSection(@ModelAttribute WarehouseSection section, RedirectAttributes redirectAttributes) {
        try {
            logger.debug("Creating new warehouse section from form: {}", section);
            section.setUsedSpace(0.0);
            section.setStatus("AVAILABLE");
            WarehouseSection savedSection = warehouseSectionRepository.save(section);
            logger.info("Successfully created warehouse section with ID: {}", savedSection.getId());
            redirectAttributes.addFlashAttribute("message", "Section created successfully!");
            return "redirect:/warehouse";
        } catch (Exception e) {
            logger.error("Error creating warehouse section", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create section: " + e.getMessage());
            return "redirect:/warehouse";
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<WarehouseSection> getSection(@PathVariable Long id) {
        try {
            logger.debug("Fetching warehouse section with ID: {}", id);
            return ResponseEntity.ok(warehouseSectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Section not found with id: " + id)));
        } catch (Exception e) {
            logger.error("Error fetching warehouse section with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch warehouse section: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<WarehouseSection> updateSection(@PathVariable Long id, @RequestBody WarehouseSection section) {
        try {
            logger.debug("Updating warehouse section with ID: {}", id);
            
            // Find the existing section
            WarehouseSection existingSection = warehouseSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found with id: " + id));
            
            // Update only the fields that are provided
            if (section.getName() != null) {
                existingSection.setName(section.getName());
            }
            if (section.getCode() != null) {
                existingSection.setCode(section.getCode());
            }
            if (section.getCapacity() != null) {
                existingSection.setCapacity(section.getCapacity());
            }
            
            // Save the updated section
            WarehouseSection updatedSection = warehouseSectionRepository.save(existingSection);
            logger.info("Successfully updated warehouse section with ID: {}", id);
            return ResponseEntity.ok(updatedSection);
        } catch (Exception e) {
            logger.error("Error updating warehouse section with ID: {}", id, e);
            throw new RuntimeException("Failed to update warehouse section: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        try {
            logger.debug("Deleting warehouse section with ID: {}", id);
            if (!warehouseSectionRepository.existsById(id)) {
                throw new RuntimeException("Section not found with id: " + id);
            }
            warehouseSectionRepository.deleteById(id);
            logger.info("Successfully deleted warehouse section with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting warehouse section with ID: {}", id, e);
            throw new RuntimeException("Failed to delete warehouse section: " + e.getMessage());
        }
    }
} 