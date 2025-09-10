package com.heroes.repository;

import com.heroes.HeroesManagementApplication;
import com.heroes.config.TestConfig;
import com.heroes.model.entity.Hero;
import com.heroes.model.entity.Superpoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = HeroesManagementApplication.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class HeroRepositoryTest {

    @Autowired
    private HeroRepository heroRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Hero hero1;
    private Hero hero2;
    private Hero hero3;
    private Superpoder superpoder1;
    private Superpoder superpoder2;
    private Superpoder superpoder3;

    @BeforeEach
    void setUp() {
        createTestData();
    }

    private void createTestData() {
        superpoder1 = new Superpoder("Super Força", "Capacidade de levantar objetos extremamente pesados");
        superpoder2 = new Superpoder("Voo", "Capacidade de voar pelos céus");
        superpoder3 = new Superpoder("Visão de Calor", "Capacidade de emitir raios de calor pelos olhos");

        entityManager.persist(superpoder1);
        entityManager.persist(superpoder2);
        entityManager.persist(superpoder3);
        entityManager.flush();

        hero1 = new Hero("Clark Kent", "Superman", LocalDate.of(1938, 4, 18), 1.91, 107.0);
        hero1.setSuperpoderes(Set.of(superpoder1, superpoder2));

        hero2 = new Hero("Bruce Wayne", "Batman", LocalDate.of(1939, 3, 30), 1.88, 95.0);
        hero2.setSuperpoderes(Set.of(superpoder1));

        hero3 = new Hero("Peter Parker", "Homem-Aranha", LocalDate.of(1962, 8, 10), 1.78, 76.0);
        hero3.setSuperpoderes(Set.of(superpoder1, superpoder3));

        entityManager.persist(hero1);
        entityManager.persist(hero2);
        entityManager.persist(hero3);
        entityManager.flush();
    }

    @Test
    void testFindByNomeHeroi() {
        String heroName = "Superman";

        Optional<Hero> result = heroRepository.findByNomeHeroi(heroName);

        assertTrue(result.isPresent());
        assertEquals(hero1.getId(), result.get().getId());
        assertEquals("Clark Kent", result.get().getNome());
        assertEquals("Superman", result.get().getNomeHeroi());
        assertEquals(2, result.get().getSuperpoderes().size());
    }

    @Test
    void testFindByNomeHeroiCaseInsensitive() {
        String heroName = "Superman";

        Optional<Hero> result = heroRepository.findByNomeHeroi(heroName);

        assertTrue(result.isPresent());
        assertEquals(hero1.getId(), result.get().getId());
        assertEquals("Superman", result.get().getNomeHeroi());
    }

    @Test
    void testFindByNomeHeroiNotFound() {
        String heroName = "Wonder Woman";

        Optional<Hero> result = heroRepository.findByNomeHeroi(heroName);

        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByNomeHeroi() {
        String existingHeroName = "Superman";
        String nonExistingHeroName = "Wonder Woman";

        boolean existsSuperman = heroRepository.existsByNomeHeroi(existingHeroName);
        boolean existsWonderWoman = heroRepository.existsByNomeHeroi(nonExistingHeroName);

        assertTrue(existsSuperman);
        assertFalse(existsWonderWoman);
    }

    @Test
    void testExistsByNomeHeroiCaseInsensitive() {
        String heroName = "Superman";

        boolean exists = heroRepository.existsByNomeHeroi(heroName);
        assertTrue(exists);
    }

    @Test
    void testExistsByNomeHeroiAndIdNot() {
        String heroName = "Superman";
        Long heroId = hero1.getId();
        Long differentHeroId = hero2.getId();

        boolean existsWithSameId = heroRepository.existsByNomeHeroiAndIdNot(heroName, heroId);
        boolean existsWithDifferentId = heroRepository.existsByNomeHeroiAndIdNot(heroName, differentHeroId);

        assertFalse(existsWithSameId);
        assertTrue(existsWithDifferentId);
    }

    @Test
    void testFindAllWithSuperpoderes() {
        List<Hero> result = heroRepository.findAllWithSuperpoderes();

        assertNotNull(result);
        assertTrue(result.size() >= 3);

        for (Hero hero : result) {
            assertNotNull(hero.getSuperpoderes());
            assertFalse(hero.getSuperpoderes().isEmpty());
        }

        Hero superman = result.stream()
                .filter(h -> "Superman".equals(h.getNomeHeroi()))
                .findFirst()
                .orElse(null);
        assertNotNull(superman);
        assertEquals(2, superman.getSuperpoderes().size());
    }

    @Test
    void testFindByIdWithSuperpoderes() {
        Optional<Hero> result = heroRepository.findByIdWithSuperpoderes(hero1.getId());

        assertTrue(result.isPresent());
        Hero hero = result.get();
        assertEquals(hero1.getId(), hero.getId());
        assertEquals("Clark Kent", hero.getNome());
        assertEquals("Superman", hero.getNomeHeroi());
        assertNotNull(hero.getSuperpoderes());
        assertEquals(2, hero.getSuperpoderes().size());
    }

    @Test
    void testFindByIdWithSuperpoderesNotFound() {
        Optional<Hero> result = heroRepository.findByIdWithSuperpoderes(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        List<Hero> result = heroRepository.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.stream().anyMatch(h -> "Superman".equals(h.getNomeHeroi())));
        assertTrue(result.stream().anyMatch(h -> "Batman".equals(h.getNomeHeroi())));
        assertTrue(result.stream().anyMatch(h -> "Homem-Aranha".equals(h.getNomeHeroi())));
    }

    @Test
    void testFindById() {
        Optional<Hero> result = heroRepository.findById(hero1.getId());
        assertTrue(result.isPresent());
        assertEquals(hero1.getId(), result.get().getId());
        assertEquals("Clark Kent", result.get().getNome());
        assertEquals("Superman", result.get().getNomeHeroi());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Hero> result = heroRepository.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSave() {
        Hero newHero = new Hero("Diana Prince", "Wonder Woman", LocalDate.of(1941, 10, 25), 1.83, 74.0);
        newHero.setSuperpoderes(Set.of(superpoder1, superpoder2));

        Hero savedHero = heroRepository.save(newHero);
        entityManager.flush();

        assertNotNull(savedHero.getId());
        assertEquals("Diana Prince", savedHero.getNome());
        assertEquals("Wonder Woman", savedHero.getNomeHeroi());
        assertEquals(2, savedHero.getSuperpoderes().size());

        Optional<Hero> retrievedHero = heroRepository.findById(savedHero.getId());
        assertTrue(retrievedHero.isPresent());
        assertEquals("Wonder Woman", retrievedHero.get().getNomeHeroi());
    }

    @Test
    void testDelete() {
        Long heroId = hero3.getId();

        heroRepository.deleteById(heroId);
        entityManager.flush();

        Optional<Hero> result = heroRepository.findById(heroId);
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsById() {
        Long existingId = hero1.getId();
        Long nonExistingId = 999L;
        boolean existsExisting = heroRepository.existsById(existingId);
        boolean existsNonExisting = heroRepository.existsById(nonExistingId);

        assertTrue(existsExisting);
        assertFalse(existsNonExisting);
    }

    @Test
    void testCount() {
        long count = heroRepository.count();

        assertEquals(3, count);
    }

    @Test
    void testCountAfterDelete() {
        heroRepository.deleteById(hero1.getId());
        entityManager.flush();

        long count = heroRepository.count();
        assertEquals(2, count);
    }

    @Test
    void testCountAfterSave() {
        Hero newHero = new Hero("Barry Allen", "Flash", LocalDate.of(1940, 1, 1), 1.83, 88.0);
        heroRepository.save(newHero);
        entityManager.flush();

        long count = heroRepository.count();

        assertEquals(4, count);
    }

    @Test
    void testFindAllEmpty() {
        heroRepository.deleteAll();
        entityManager.flush();

        List<Hero> result = heroRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveWithDuplicateNomeHeroi() {
        Hero duplicateHero = new Hero("Clark Kent Jr", "Superman", LocalDate.of(1990, 1, 1), 1.80, 90.0);

        try {
            heroRepository.save(duplicateHero);
            entityManager.flush();
            fail("Should have thrown an exception for duplicate nomeHeroi");
        } catch (Exception e) {
            assertNotNull(e.getCause());
        }
    }

    @Test
    void testFindByNomeHeroiWithSpecialCharacters() {
        Hero heroWithSpecialChars = new Hero("José da Silva", "Herói-Brasileiro", LocalDate.of(1980, 5, 15), 1.75, 80.0);
        heroRepository.save(heroWithSpecialChars);
        entityManager.flush();

        Optional<Hero> result = heroRepository.findByNomeHeroi("Herói-Brasileiro");

        assertTrue(result.isPresent());
        assertEquals("José da Silva", result.get().getNome());
        assertEquals("Herói-Brasileiro", result.get().getNomeHeroi());
    }
}
