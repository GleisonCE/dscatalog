package com.ms9.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.ms9.dscatalog.entities.Product;
import com.ms9.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	private long existingId;
	private long nonExistingId;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 1000L;
	}
	
	@Test
	public void saveShouldPersistWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		product = repo.save(product);
		Assertions.assertNotNull(product.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {		
		repo.deleteById(existingId);
		Optional<Product> result = repo.findById(existingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowsEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {			
			repo.deleteById(nonExistingId);			
		});
	}
	
	@Test
	public void searchShouldReturnEmptyObjectWhenIdDoesNotExist() {
		Optional<Product> result = repo.findById(nonExistingId);
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void searchShouldReturnObjectWhenIdExist() {
		Optional<Product> result = repo.findById(existingId);
		Assertions.assertTrue(result.isPresent());
	}
}
