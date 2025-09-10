package com.heroes.repository;

import com.heroes.model.entity.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
    
    Optional<Hero> findByNomeHeroi(String nomeHeroi);
    
    boolean existsByNomeHeroi(String nomeHeroi);
    
    boolean existsByNomeHeroiAndIdNot(String nomeHeroi, Long id);
    
    @Query("SELECT h FROM Hero h LEFT JOIN FETCH h.superpoderes")
    List<Hero> findAllWithSuperpoderes();
    
    @Query("SELECT h FROM Hero h LEFT JOIN FETCH h.superpoderes WHERE h.id = :id")
    Optional<Hero> findByIdWithSuperpoderes(@Param("id") Long id);
}
