package com.bookstore.inventory_service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Inventory {

	/** Trùng với id của sách bên book-service. */
	@Id
	private Long bookId;

	private int quantity;
}
