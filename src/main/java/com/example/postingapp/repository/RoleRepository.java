package com.example.postingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	public Role findByName(String name);
}