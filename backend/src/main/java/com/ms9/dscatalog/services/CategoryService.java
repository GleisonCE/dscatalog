package com.ms9.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms9.dscatalog.entities.Category;
import com.ms9.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repo;
	
	@Transactional(readOnly = true)
	public List<Category> findAll() {
		return repo.findAll();
	}
}