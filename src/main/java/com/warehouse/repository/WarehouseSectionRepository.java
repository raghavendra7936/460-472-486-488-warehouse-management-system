package com.warehouse.repository;

import com.warehouse.model.WarehouseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseSectionRepository extends JpaRepository<WarehouseSection, Long> {
} 