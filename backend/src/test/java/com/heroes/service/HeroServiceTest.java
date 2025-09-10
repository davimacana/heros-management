package com.heroes.service;

import com.heroes.config.exception.DuplicateHeroNameException;
import com.heroes.config.exception.ResourceNotFoundException;
import com.heroes.model.dto.HeroRequestDTO;
import com.heroes.model.dto.HeroResponseDTO;
import com.heroes.model.dto.SuperpoderDTO;
import com.heroes.model.entity.Hero;
import com.heroes.model.entity.Superpoder;
import com.heroes.model.mapper.HeroMapper;
import com.heroes.repository.HeroRepository;
import com.heroes.repository.SuperpoderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeroServiceTest {

    @Mock
    private HeroRepository heroRepository;

    @Mock
    private SuperpoderRepository superpoderRepository;

    @Mock
    private HeroMapper heroMapper;

    @InjectMocks
    private HeroService heroService;

    private Hero hero1;
    private Hero hero2;
    private Superpoder superpoder1;
    private Superpoder superpoder2;
    private HeroRequestDTO heroRequestDTO;
    private HeroResponseDTO heroResponseDTO;

    @BeforeEach
    void setUp() {
        superpoder1 = new Superpoder("Super Força", "Capacidade de levantar objetos extremamente pesados");
        superpoder1.setId(1L);

        superpoder2 = new Superpoder("Voo", "Capacidade de voar pelos céus");
        superpoder2.setId(2L);

        hero1 = new Hero("Clark Kent", "Superman", LocalDate.of(1938, 4, 18), 1.91, 107.0);
        hero1.setId(1L);
        hero1.setSuperpoderes(Set.of(superpoder1, superpoder2));

        hero2 = new Hero("Bruce Wayne", "Batman", LocalDate.of(1939, 3, 30), 1.88, 95.0);
        hero2.setId(2L);
        hero2.setSuperpoderes(Set.of(superpoder1));

        heroRequestDTO = new HeroRequestDTO(
            "Peter Parker",
            "Homem-Aranha",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
            Arrays.asList(1L, 2L)
        );

        SuperpoderDTO superpoderDTO1 = new SuperpoderDTO(1L, "Super Força", "Capacidade de levantar objetos extremamente pesados");
        SuperpoderDTO superpoderDTO2 = new SuperpoderDTO(2L, "Voo", "Capacidade de voar pelos céus");

        heroResponseDTO = new HeroResponseDTO(
            3L,
            "Peter Parker",
            "Homem-Aranha",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
            Arrays.asList(superpoderDTO1, superpoderDTO2)
        );
    }

    @Test
    void testFindAllHeroes() {
        List<Hero> heroes = Arrays.asList(hero1, hero2);
        when(heroRepository.findAllWithSuperpoderes()).thenReturn(heroes);

        List<HeroResponseDTO> result = heroService.findAllHeroes();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(heroRepository).findAllWithSuperpoderes();
    }

    @Test
    void testFindHeroById() {
        when(heroRepository.findByIdWithSuperpoderes(1L)).thenReturn(Optional.of(hero1));

        HeroResponseDTO result = heroService.findHeroById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Clark Kent", result.nome());
        assertEquals("Superman", result.nomeHeroi());
        verify(heroRepository).findByIdWithSuperpoderes(1L);
    }

    @Test
    void testFindHeroByIdNotFound() {
        when(heroRepository.findByIdWithSuperpoderes(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> heroService.findHeroById(999L));
    }

    @Test
    void testCreateHero() {
        Hero newHero = new Hero("Peter Parker", "Homem-Aranha", LocalDate.of(1962, 8, 10), 1.78, 76.0);
        newHero.setId(3L);
        newHero.setSuperpoderes(Set.of(superpoder1, superpoder2));

        when(heroRepository.existsByNomeHeroi("Homem-Aranha")).thenReturn(false);
        when(superpoderRepository.findById(1L)).thenReturn(Optional.of(superpoder1));
        when(superpoderRepository.findById(2L)).thenReturn(Optional.of(superpoder2));
        when(heroMapper.toEntity(heroRequestDTO, Set.of(superpoder1, superpoder2))).thenReturn(newHero);
        when(heroRepository.save(newHero)).thenReturn(newHero);
        when(heroRepository.findByIdWithSuperpoderes(3L)).thenReturn(Optional.of(newHero));

        HeroResponseDTO result = heroService.createHero(heroRequestDTO);

        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Peter Parker", result.nome());
        assertEquals("Homem-Aranha", result.nomeHeroi());
        verify(heroRepository).existsByNomeHeroi("Homem-Aranha");
        verify(superpoderRepository).findById(1L);
        verify(superpoderRepository).findById(2L);
        verify(heroRepository).save(newHero);
    }

    @Test
    void testCreateHeroWithDuplicateName() {
        when(heroRepository.existsByNomeHeroi("Superman")).thenReturn(true);

        HeroRequestDTO duplicateRequest = new HeroRequestDTO(
            "Clark Kent",
            "Superman",
            LocalDate.of(1938, 4, 18),
            1.91,
            107.0,
            Arrays.asList(1L)
        );

        assertThrows(DuplicateHeroNameException.class, () -> heroService.createHero(duplicateRequest));
    }

    @Test
    void testCreateHeroWithInvalidSuperpoderId() {
        when(heroRepository.existsByNomeHeroi("Homem-Aranha")).thenReturn(false);
        when(superpoderRepository.findById(999L)).thenReturn(Optional.empty());

        HeroRequestDTO invalidRequest = new HeroRequestDTO(
            "Peter Parker",
            "Homem-Aranha",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
            Arrays.asList(999L)
        );

        assertThrows(ResourceNotFoundException.class, () -> heroService.createHero(invalidRequest));
    }

    @Test
    void testUpdateHero() {
        HeroRequestDTO updateRequest = new HeroRequestDTO(
            "Peter Parker",
            "Spider-Man",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
            Arrays.asList(1L)
        );

        Hero updatedHero = new Hero("Peter Parker", "Spider-Man", LocalDate.of(1962, 8, 10), 1.78, 76.0);
        updatedHero.setId(1L);
        updatedHero.setSuperpoderes(Set.of(superpoder1));

        HeroResponseDTO updatedResponseDTO = new HeroResponseDTO(
            1L,
            "Peter Parker",
            "Spider-Man",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
                List.of(new SuperpoderDTO(1L, "Super Força", "Capacidade de levantar objetos extremamente pesados"))
        );

        when(heroRepository.findById(1L)).thenReturn(Optional.of(hero1));
        when(heroRepository.existsByNomeHeroiAndIdNot("Spider-Man", 1L)).thenReturn(false);
        when(superpoderRepository.findById(1L)).thenReturn(Optional.of(superpoder1));
        when(heroRepository.save(hero1)).thenReturn(updatedHero);
        when(heroRepository.findByIdWithSuperpoderes(1L)).thenReturn(Optional.of(updatedHero));

        HeroResponseDTO result = heroService.updateHero(1L, updateRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Peter Parker", result.nome());
        assertEquals("Spider-Man", result.nomeHeroi());
        verify(heroRepository).findById(1L);
        verify(heroRepository).existsByNomeHeroiAndIdNot("Spider-Man", 1L);
        verify(heroRepository).save(hero1);
    }

    @Test
    void testUpdateHeroNotFound() {
        when(heroRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> heroService.updateHero(999L, heroRequestDTO));
    }

    @Test
    void testUpdateHeroWithDuplicateName() {
        HeroRequestDTO updateRequest = new HeroRequestDTO(
            "Bruce Wayne",
            "Superman",
            LocalDate.of(1939, 3, 30),
            1.88,
            95.0,
                List.of(1L)
        );

        when(heroRepository.findById(2L)).thenReturn(Optional.of(hero2));
        when(heroRepository.existsByNomeHeroiAndIdNot("Superman", 2L)).thenReturn(true);

        assertThrows(DuplicateHeroNameException.class, () -> heroService.updateHero(2L, updateRequest));
    }

    @Test
    void testRemoveHero() {
        when(heroRepository.existsById(1L)).thenReturn(true);

        heroService.removeHero(1L);

        verify(heroRepository).existsById(1L);
        verify(heroRepository).deleteById(1L);
    }

    @Test
    void testRemoveHeroNotFound() {
        when(heroRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> heroService.removeHero(999L));
    }

    @Test
    void testFindAllHeroesEmpty() {
        when(heroRepository.findAllWithSuperpoderes()).thenReturn(Collections.emptyList());

        List<HeroResponseDTO> result = heroService.findAllHeroes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(heroRepository).findAllWithSuperpoderes();
        verify(heroMapper, never()).toResponseDTO(any(Hero.class));
    }
}
