package com.kai.InventoryManagementSystem.Service;


import com.kai.InventoryManagementSystem.DTO.InventoryDTO;
import com.kai.InventoryManagementSystem.Repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.kai.InventoryManagementSystem.Entity.InventoryItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public InventoryService (InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryDTO convertToDTO(InventoryItem item) {

        if (item == null) {
            logger.error("Cannot convert null InventoryItem to DTO");
            return null;
        }

        logger.debug("Converting InventoryItem to DTO: {}", item);

        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(item.getId());
        inventoryDTO.setName(item.getName());
        inventoryDTO.setQuantity(item.getQuantity());
        inventoryDTO.setPrice(item.getPrice());
        inventoryDTO.setCategory(item.getCategory());

        logger.debug("Converted InventoryDTO: {}", inventoryDTO);
        return inventoryDTO;
    }

    //CRUD Operations
    //Create = Save method| Read = Get method| Update = Update method| Delete = Delete method
    public List<InventoryDTO> getAllItems() {
        logger.info("Fetching all inventory items");
        List<InventoryDTO> items = inventoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} inventory items", items.size());
        return items;
    }

    public InventoryDTO getItemById(Long id) {
        logger.info("Fetching inventory item with ID: {}", id);
        InventoryDTO item = inventoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);

        if (item == null) {
            logger.warn("Item with ID {} not found", id);
        } else {
            logger.info("Fetched inventory item: {}", item);
        }

        return item;
    }

    public List<InventoryDTO> getItemsByName(String name) {
        logger.info("Fetching inventory items by name: {}", name);
        List<InventoryDTO> items = inventoryRepository.findByNameContainingIgnoreCase(name).stream()
                .filter(Objects::nonNull) // Safeguard against null values
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Found {} items matching name: {}", items.size(), name);
        return items;
    }

    public List<InventoryDTO> filterItemsByCategory(String category) {
        logger.info("Filtering items by category: {}", category);
        List<InventoryDTO> items = inventoryRepository.findByCategoryContainingIgnoreCase(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Found {} items in category: {}", items.size(), category);
        return items;
    }

    public List<InventoryDTO> filterItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Filtering items by price range: {} - {}", minPrice, maxPrice);
        List<InventoryDTO> items = inventoryRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Found {} items in price range: {} - {}", items.size(), minPrice, maxPrice);
        return items;
    }

    public InventoryDTO restockItem(Long id, int quantity) {
        logger.info("Restocking item with ID: {} by quantity: {}", id, quantity);
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item ID: "));

        item.setQuantity(item.getQuantity() + quantity);
        item.setLastUpdated(LocalDateTime.now());

        InventoryItem savedItem = inventoryRepository.save(item);
        InventoryDTO savedItemDTO = convertToDTO(savedItem);

        logger.info("Restocked item: {}", savedItemDTO);
        return savedItemDTO;

    }

    public List<InventoryDTO> bulkSaveItems(List<InventoryItem> items) {
        logger.info("Saving {} inventory items in bulk", items.size());
        List<InventoryItem> savedItems = inventoryRepository.saveAll(items);
        List<InventoryDTO> savedDTOs = savedItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Bulk saved items");
        return savedDTOs;
    }

    public void bulkDeleteItems(List<Long> ids) {
        logger.info("Deleting {} inventory items in bulk", ids.size());
        List<InventoryItem> itemsToDelete = inventoryRepository.findAllById(ids);
        inventoryRepository.deleteAll(itemsToDelete);
        logger.info("Deleted items with IDs: {}", ids);

    }

    public InventoryDTO saveItem(InventoryItem item) {
        logger.info("Saving new inventory item: {}", item);
        item.setLastUpdated(LocalDateTime.now());
        InventoryItem savedItem = inventoryRepository.save(item);
        InventoryDTO savedDTO = convertToDTO(savedItem);
        logger.info("Saved inventory item: {}", savedDTO);
        return savedDTO;
    }

    public List<InventoryDTO> getLowStockItems(int threshold) {
        logger.info("Fetching items with stock below threshold: {}", threshold);
        List<InventoryDTO> lowStockItems = inventoryRepository.findByQuantityLessThan(threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    public InventoryDTO updateItem(Long id, InventoryItem item) {
        logger.info("Updating inventory item with ID: {}", id);
        InventoryItem itemToUpdate = inventoryRepository.findById(id)
                        .orElseThrow(() -> {
                            logger.error("Item ID {} not found", id);
                            return new EntityNotFoundException("Item ID: " + id + " not found");
                        });

        logger.debug("Original item details: {}", itemToUpdate);

        itemToUpdate.setName(item.getName());
        itemToUpdate.setQuantity(item.getQuantity());
        itemToUpdate.setPrice(item.getPrice());
        itemToUpdate.setCategory(item.getCategory());
        itemToUpdate.setLastUpdated(LocalDateTime.now());

        InventoryItem savedItem = inventoryRepository.save(itemToUpdate);
        InventoryDTO savedItemDTO = convertToDTO(savedItem);

        logger.info("Updated inventory item: {}", savedItemDTO);
        return savedItemDTO;
    }

    public void deleteItem(Long id) {
        logger.info("Deleting inventory item with ID: {}", id);
        InventoryItem itemToDelete = inventoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Item ID {} not found", id);
                    return new EntityNotFoundException("Item ID: " + id + " not found");
                });

        inventoryRepository.delete(itemToDelete);
        logger.info("Deleted inventory item with ID: {}", id);
    }
}
