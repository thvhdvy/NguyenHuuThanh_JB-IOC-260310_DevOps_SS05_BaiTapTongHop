package com.bookstore.inventory_service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryRepository repository;

	public InventoryController(InventoryRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/ping")
	public Map<String, String> ping() {
		return Map.of("service", "inventory-service", "status", "UP");
	}

	@GetMapping
	public List<Inventory> findAll() {
		return repository.findAll();
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<Inventory> findByBookId(@PathVariable Long bookId) {
		return repository.findById(bookId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{bookId}")
	public Inventory upsert(@PathVariable Long bookId, @RequestBody Inventory body) {
		body.setBookId(bookId);
		return repository.save(body);
	}
}
