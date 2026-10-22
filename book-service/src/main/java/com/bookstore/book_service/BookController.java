package com.bookstore.book_service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookRepository repository;
	private final RestClient inventoryClient;

	public BookController(BookRepository repository, @Value("${inventory.url}") String inventoryUrl) {
		this.repository = repository;
		this.inventoryClient = RestClient.builder().baseUrl(inventoryUrl).build();
	}

	@GetMapping("/ping")
	public Map<String, String> ping() {
		return Map.of("service", "book-service", "status", "UP");
	}

	@GetMapping
	public List<Book> findAll() {
		return repository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> findById(@PathVariable Long id) {
		return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Book create(@RequestBody Book book) {
		book.setId(null);
		return repository.save(book);
	}

	/** Kiểm tra gọi liên service: book-service -> inventory-service. */
	@GetMapping("/{id}/stock")
	public ResponseEntity<?> stock(@PathVariable Long id) {
		if (!repository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		try {
			Object stock = inventoryClient.get().uri("/api/inventory/{id}", id).retrieve().body(Object.class);
			return ResponseEntity.ok(stock);
		} catch (RestClientException e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body(Map.of("error", "inventory-service unavailable or has no record", "detail", String.valueOf(e.getMessage())));
		}
	}
}
