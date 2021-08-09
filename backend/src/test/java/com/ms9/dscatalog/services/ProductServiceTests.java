package com.ms9.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ms9.dscatalog.dto.ProductDTO;
import com.ms9.dscatalog.entities.Product;
import com.ms9.dscatalog.repositories.ProductRepository;
import com.ms9.dscatalog.services.exceptions.DatabaseException;
import com.ms9.dscatalog.services.exceptions.ResourceNotFoundException;
import com.ms9.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repo;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
		
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct(); 
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repo.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repo.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repo.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repo.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.doNothing().when(repo).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repo).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repo).deleteById(dependentId);
	}
	
	@Test
	public void findAllPageShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged((long) 0, "", pageable);
		Assertions.assertNotNull(result);
		Mockito.verify(repo).findAll(pageable);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependecy() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repo, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repo, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repo, Mockito.times(1)).deleteById(existingId);
	}
}
