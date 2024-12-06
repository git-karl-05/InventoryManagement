package com.kai.InventoryManagementSystem.Repository;

import com.kai.InventoryManagementSystem.DTO.InventoryDTO;
import com.kai.InventoryManagementSystem.Entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    InventoryDTO getInventoryItemsByName(String name);

    List<InventoryDTO> findByNameContainingIgnoreCase(String name);

    List<InventoryDTO> findByCategoryContainingIgnoreCase(String category);

    List<InventoryDTO> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<InventoryDTO> findByQuantityLessThan(int threshold);
}
