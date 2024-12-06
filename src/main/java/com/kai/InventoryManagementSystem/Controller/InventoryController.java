package com.kai.InventoryManagementSystem.Controller;


import com.kai.InventoryManagementSystem.DTO.InventoryDTO;
import com.kai.InventoryManagementSystem.Entity.InventoryItem;
import com.kai.InventoryManagementSystem.Service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/items")
@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController (InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllItems() {
        List<InventoryDTO> items = inventoryService.getAllItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getItemById(@PathVariable Long id) {
        InventoryDTO item = inventoryService.getItemById(id);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> saveItem( @RequestBody InventoryItem item) {
        InventoryDTO savedItem = inventoryService.saveItem(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateItem(@PathVariable Long id, @RequestBody InventoryItem item) {
        InventoryDTO updatedItem = inventoryService.updateItem(id, item);
        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return new ResponseEntity<>("Item successfully deleted", HttpStatus.NO_CONTENT);

    }
}
