package com.heroes.repository;

import com.heroes.model.entity.Superpoder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperpoderRepository extends JpaRepository<Superpoder, Long> {
    
    Optional<Superpoder> findByNome(String nome);
    
    boolean existsByNome(String nome);
}
