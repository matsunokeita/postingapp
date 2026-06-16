package com.example.postingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.VerificationToken;


public interface VerificationTokenRepository extends JpaRepository< VerificationToken, Integer> {
    public VerificationToken findByToken(String token);
}