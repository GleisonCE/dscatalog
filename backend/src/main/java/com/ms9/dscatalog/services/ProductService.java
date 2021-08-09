package com.ms9.dscatalog.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms9.dscatalog.dto.CategoryDTO;
import com.ms9.dscatalog.dto.ProductDTO;
import com.ms9.dscatalog.entities.Category;
import com.ms9.dscatalog.entities.Product;
import com.ms9.dscatalog.repositories.CategoryRepository;
import com.ms9.dscatalog.repositories.ProductRepository;
import com.ms9.dscatalog.services.exceptions.DatabaseException;
import com.ms9.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repo;
	@Autowired
	private CategoryRepository repoCategory;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Long categoryId, String name, Pageable pageable) {
		List<Category> categories= (categoryId == 0) ? null : Arrays.asList(repoCategory.getOne(categoryId));
		Page<Product> list = repo.find(categories, name, pageable);
		//repo.findProductWithCategories(list.getContent()); //Problema das consulta N+1, para o construtor do produto com categoria
		return list.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repo.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repo.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repo.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repo.save(entity);
			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
	}

	public void delete(Long id) {
		try {
			repo.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		entity.setDate(dto.getDate());
		
		entity.getCategories().clear();
		for (CategoryDTO catDto: dto.getCategories()) {
			Category cat = repoCategory.getOne(catDto.getId());
			entity.getCategories().add(cat);
		}
	}

}
