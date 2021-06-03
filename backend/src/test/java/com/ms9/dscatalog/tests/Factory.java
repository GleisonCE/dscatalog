package com.ms9.dscatalog.tests;

import java.time.Instant;

import com.ms9.dscatalog.dto.ProductDTO;
import com.ms9.dscatalog.entities.Category;
import com.ms9.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com.img.png", Instant.parse("2021-05-30T03:00:00Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
}
