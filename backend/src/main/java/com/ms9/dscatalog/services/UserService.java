package com.ms9.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms9.dscatalog.dto.RoleDTO;
import com.ms9.dscatalog.dto.UserDTO;
import com.ms9.dscatalog.dto.UserInsertDTO;
import com.ms9.dscatalog.dto.UserUpdateDTO;
import com.ms9.dscatalog.entities.Role;
import com.ms9.dscatalog.entities.User;
import com.ms9.dscatalog.repositories.RoleRepository;
import com.ms9.dscatalog.repositories.UserRepository;
import com.ms9.dscatalog.services.exceptions.DatabaseException;
import com.ms9.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {

	private static Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserRepository repo;
	@Autowired
	private RoleRepository repoRole;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repo.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repo.findById(id);
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity.setPassword(encoder.encode(dto.getPassword()));
		entity = repo.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repo.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repo.save(entity);
			return new UserDTO(entity);
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
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
				
		entity.getRoles().clear();
		for (RoleDTO roleDto: dto.getRoles()) {
			Role role = repoRole.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.findByEmail(username);
		if (user == null) {
			logger.error("User not Found: "+ username);
			throw new UsernameNotFoundException("Email not found");
		}		
		logger.info("User found: "+username);
		return user;
	}

}
