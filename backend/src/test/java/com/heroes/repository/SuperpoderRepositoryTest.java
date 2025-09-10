package com.heroes.repository;

import com.heroes.HeroesManagementApplication;
import com.heroes.config.TestConfig;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = HeroesManagementApplication.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class SuperpoderRepositoryTest {

    @Autowired
    private SuperpoderRepository superpoderRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
    }

    @Test
    void testFindByNome() {
        String superpoderName = "Super Força";

        Optional<Superpoder> result = superpoderRepository.findByNome(superpoderName);

        assertTrue(result.isPresent());
        assertEquals(superpoder1.getId(), result.get().getId());
        assertEquals("Super Força", result.get().getNome());
        assertEquals("Capacidade de levantar objetos extremamente pesados", result.get().getDescricao());
    }

    @Test
    void testFindByNomeCaseInsensitive() {
        String superpoderName = "Super Força";

        Optional<Superpoder> result = superpoderRepository.findByNome(superpoderName);

        assertTrue(result.isPresent());
        assertEquals(superpoder1.getId(), result.get().getId());
        assertEquals("Super Força", result.get().getNome());
    }

    @Test
    void testFindByNomeNotFound() {
        String superpoderName = "Telepatia";

        Optional<Superpoder> result = superpoderRepository.findByNome(superpoderName);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByNomeWithSpecialCharacters() {
        Superpoder superpoderWithSpecialChars = new Superpoder("Superpoder Especial & Único", "Descrição com caracteres especiais: áéíóú, ç, ñ");
        entityManager.persist(superpoderWithSpecialChars);
        entityManager.flush();

        Optional<Superpoder> result = superpoderRepository.findByNome("Superpoder Especial & Único");

        assertTrue(result.isPresent());
        assertEquals("Superpoder Especial & Único", result.get().getNome());
        assertEquals("Descrição com caracteres especiais: áéíóú, ç, ñ", result.get().getDescricao());
    }

    @Test
    void testExistsByNome() {
        String existingName = "Super Força";
        String nonExistingName = "Telepatia";

        boolean existsSuperForca = superpoderRepository.existsByNome(existingName);
        boolean existsTelepatia = superpoderRepository.existsByNome(nonExistingName);

        assertTrue(existsSuperForca);
        assertFalse(existsTelepatia);
    }

    @Test
    void testExistsByNomeCaseInsensitive() {
        String superpoderName = "Super Força";

        boolean exists = superpoderRepository.existsByNome(superpoderName);

        assertTrue(exists);
    }

    @Test
    void testFindAll() {
        List<Superpoder> result = superpoderRepository.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.stream().anyMatch(s -> "Super Força".equals(s.getNome())));
        assertTrue(result.stream().anyMatch(s -> "Voo".equals(s.getNome())));
        assertTrue(result.stream().anyMatch(s -> "Visão de Calor".equals(s.getNome())));
    }

    @Test
    void testFindById() {
        Optional<Superpoder> result = superpoderRepository.findById(superpoder1.getId());

        assertTrue(result.isPresent());
        assertEquals(superpoder1.getId(), result.get().getId());
        assertEquals("Super Força", result.get().getNome());
        assertEquals("Capacidade de levantar objetos extremamente pesados", result.get().getDescricao());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Superpoder> result = superpoderRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSave() {
        Superpoder newSuperpoder = new Superpoder("Telepatia", "Capacidade de ler mentes");
        Superpoder savedSuperpoder = superpoderRepository.save(newSuperpoder);
        entityManager.flush();
        assertNotNull(savedSuperpoder.getId());
        assertEquals("Telepatia", savedSuperpoder.getNome());
        assertEquals("Capacidade de ler mentes", savedSuperpoder.getDescricao());

        Optional<Superpoder> retrievedSuperpoder = superpoderRepository.findById(savedSuperpoder.getId());
        assertTrue(retrievedSuperpoder.isPresent());
        assertEquals("Telepatia", retrievedSuperpoder.get().getNome());
    }

    @Test
    void testSaveWithNullDescription() {
        Superpoder superpoderWithNullDesc = new Superpoder("Superpoder Sem Descrição", null);

        Superpoder savedSuperpoder = superpoderRepository.save(superpoderWithNullDesc);
        entityManager.flush();

        assertNotNull(savedSuperpoder.getId());
        assertEquals("Superpoder Sem Descrição", savedSuperpoder.getNome());
        assertNull(savedSuperpoder.getDescricao());

        Optional<Superpoder> retrievedSuperpoder = superpoderRepository.findById(savedSuperpoder.getId());
        assertTrue(retrievedSuperpoder.isPresent());
        assertNull(retrievedSuperpoder.get().getDescricao());
    }

    @Test
    void testSaveWithEmptyDescription() {
        Superpoder superpoderWithEmptyDesc = new Superpoder("Superpoder Descrição Vazia", "");

        Superpoder savedSuperpoder = superpoderRepository.save(superpoderWithEmptyDesc);
        entityManager.flush();

        assertNotNull(savedSuperpoder.getId());
        assertEquals("Superpoder Descrição Vazia", savedSuperpoder.getNome());
        assertEquals("", savedSuperpoder.getDescricao());
    }

    @Test
    void testSaveWithLongDescription() {
        String longDescription = "Esta é uma descrição muito longa para testar como o sistema lida com textos extensos. " +
                "A descrição deve ser armazenada corretamente mesmo quando contém muitos caracteres. " +
                "Este teste verifica se não há limitações indevidas no tamanho da descrição. " +
                "A descrição pode conter até 500 caracteres conforme definido na entidade.";

        Superpoder superpoderWithLongDesc = new Superpoder("Superpoder Descrição Longa", longDescription);

        Superpoder savedSuperpoder = superpoderRepository.save(superpoderWithLongDesc);
        entityManager.flush();

        assertNotNull(savedSuperpoder.getId());
        assertEquals("Superpoder Descrição Longa", savedSuperpoder.getNome());
        assertEquals(longDescription, savedSuperpoder.getDescricao());
    }

    @Test
    void testDelete() {
        Long superpoderId = superpoder3.getId();

        superpoderRepository.deleteById(superpoderId);
        entityManager.flush();
        Optional<Superpoder> result = superpoderRepository.findById(superpoderId);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteAll() {
        superpoderRepository.deleteAll();
        entityManager.flush();

        List<Superpoder> result = superpoderRepository.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testExistsById() {
        Long existingId = superpoder1.getId();
        Long nonExistingId = 999L;

        boolean existsExisting = superpoderRepository.existsById(existingId);
        boolean existsNonExisting = superpoderRepository.existsById(nonExistingId);

        assertTrue(existsExisting);
        assertFalse(existsNonExisting);
    }

    @Test
    void testCount() {
        long count = superpoderRepository.count();
        assertEquals(3, count);
    }

    @Test
    void testCountAfterDelete() {
        superpoderRepository.deleteById(superpoder1.getId());
        entityManager.flush();

        long count = superpoderRepository.count();
        assertEquals(2, count);
    }

    @Test
    void testCountAfterSave() {
        Superpoder newSuperpoder = new Superpoder("Invisibilidade", "Capacidade de se tornar invisível");
        superpoderRepository.save(newSuperpoder);
        entityManager.flush();

        long count = superpoderRepository.count();
        assertEquals(4, count);
    }

    @Test
    void testFindAllEmpty() {
        superpoderRepository.deleteAll();
        entityManager.flush();

        List<Superpoder> result = superpoderRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveWithDuplicateNome() {
        Superpoder duplicateSuperpoder = new Superpoder("Super Força", "Descrição diferente");

        try {
            superpoderRepository.save(duplicateSuperpoder);
            entityManager.flush();
            fail("Should have thrown an exception for duplicate nome");
        } catch (Exception e) {
            assertNotNull(e.getCause());
        }
    }

    @Test
    void testFindByNomeWithAccents() {
        Superpoder superpoderWithAccents = new Superpoder("Superpoder Acentuado", "Descrição com acentos: ação, coração, situação");
        entityManager.persist(superpoderWithAccents);
        entityManager.flush();

        Optional<Superpoder> result = superpoderRepository.findByNome("Superpoder Acentuado");

        assertTrue(result.isPresent());
        assertEquals("Superpoder Acentuado", result.get().getNome());
        assertEquals("Descrição com acentos: ação, coração, situação", result.get().getDescricao());
    }

    @Test
    void testFindByNomeWithNumbers() {
        Superpoder superpoderWithNumbers = new Superpoder("Superpoder 123", "Descrição com números: 1, 2, 3");
        entityManager.persist(superpoderWithNumbers);
        entityManager.flush();

        Optional<Superpoder> result = superpoderRepository.findByNome("Superpoder 123");

        assertTrue(result.isPresent());
        assertEquals("Superpoder 123", result.get().getNome());
        assertEquals("Descrição com números: 1, 2, 3", result.get().getDescricao());
    }

    @Test
    void testFindByNomeWithSymbols() {
        Superpoder superpoderWithSymbols = new Superpoder("Superpoder @#$%", "Descrição com símbolos: @#$%&*()");
        entityManager.persist(superpoderWithSymbols);
        entityManager.flush();

        Optional<Superpoder> result = superpoderRepository.findByNome("Superpoder @#$%");

        assertTrue(result.isPresent());
        assertEquals("Superpoder @#$%", result.get().getNome());
        assertEquals("Descrição com símbolos: @#$%&*()", result.get().getDescricao());
    }
}
