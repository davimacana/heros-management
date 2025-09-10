package com.heroes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heroes.HeroesManagementApplication;
import com.heroes.config.TestConfig;
import com.heroes.model.dto.HeroRequestDTO;
import com.heroes.model.entity.Hero;
import com.heroes.model.entity.Superpoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HeroesManagementApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class HeroControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;


    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Hero hero1;
    private Hero hero2;
    private Superpoder superpoder1;
    private Superpoder superpoder2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        createTestData();
    }

    private void createTestData() {
        superpoder1 = new Superpoder("Super Força", "Capacidade de levantar objetos extremamente pesados");
        superpoder2 = new Superpoder("Voo", "Capacidade de voar pelos céus");

        entityManager.persist(superpoder1);
        entityManager.persist(superpoder2);
        entityManager.flush();

        hero1 = new Hero("Clark Kent", "Superman", LocalDate.of(1938, 4, 18), 1.91, 107.0);
        hero1.setSuperpoderes(Set.of(superpoder1, superpoder2));

        hero2 = new Hero("Bruce Wayne", "Batman", LocalDate.of(1939, 3, 30), 1.88, 95.0);
        hero2.setSuperpoderes(Set.of(superpoder1));

        entityManager.persist(hero1);
        entityManager.persist(hero2);
        entityManager.flush();
    }

    @Test
    void testGetAllHeroes() throws Exception {
        mockMvc.perform(get("/api/heroes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].nome").exists())
                .andExpect(jsonPath("$[0].nomeHeroi").exists());
    }

    @Test
    void testGetHeroById() throws Exception {
        mockMvc.perform(get("/api/heroes/{id}", hero1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(hero1.getId()))
                .andExpect(jsonPath("$.nome").value("Clark Kent"))
                .andExpect(jsonPath("$.nomeHeroi").value("Superman"))
                .andExpect(jsonPath("$.superpoderes").isArray())
                .andExpect(jsonPath("$.superpoderes.length()").value(2));
    }

    @Test
    void testGetHeroByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/heroes/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Herói não encontrado com ID: '999'"));
    }

    @Test
    void testCreateHero() throws Exception {
        HeroRequestDTO heroRequest = new HeroRequestDTO(
            "Peter Parker",
            "Homem-Aranha",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
            Arrays.asList(superpoder1.getId(), superpoder2.getId())
        );

        mockMvc.perform(post("/api/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(heroRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Peter Parker"))
                .andExpect(jsonPath("$.nomeHeroi").value("Homem-Aranha"))
                .andExpect(jsonPath("$.altura").value(1.78))
                .andExpect(jsonPath("$.peso").value(76.0))
                .andExpect(jsonPath("$.superpoderes").isArray())
                .andExpect(jsonPath("$.superpoderes.length()").value(2));
    }

    @Test
    void testCreateHeroWithInvalidData() throws Exception {
        HeroRequestDTO invalidRequest = new HeroRequestDTO(
            "",
            "",
            null,
            -1.0,
            -1.0,
            Collections.emptyList()
        );

        mockMvc.perform(post("/api/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void testCreateHeroWithDuplicateName() throws Exception {
        HeroRequestDTO duplicateRequest = new HeroRequestDTO(
            "Clark Kent",
            "Superman",
            LocalDate.of(1938, 4, 18),
            1.91,
            107.0,
                Collections.singletonList(superpoder1.getId())
        );

        mockMvc.perform(post("/api/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Nome de herói duplicado"))
                .andExpect(jsonPath("$.message").value("Já existe um herói cadastrado com o nome 'Superman'"));
    }

    @Test
    void testUpdateHero() throws Exception {
        HeroRequestDTO updateRequest = new HeroRequestDTO(
            "Peter Parker",
            "Spider-Man",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
                Collections.singletonList(superpoder1.getId())
        );

        mockMvc.perform(put("/api/heroes/{id}", hero1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(hero1.getId()))
                .andExpect(jsonPath("$.nome").value("Peter Parker"))
                .andExpect(jsonPath("$.nomeHeroi").value("Spider-Man"))
                .andExpect(jsonPath("$.superpoderes").isArray())
                .andExpect(jsonPath("$.superpoderes.length()").value(1));
    }

    @Test
    void testUpdateHeroNotFound() throws Exception {
        HeroRequestDTO updateRequest = new HeroRequestDTO(
            "Peter Parker",
            "Spider-Man",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
                Collections.singletonList(superpoder1.getId())
        );

        mockMvc.perform(put("/api/heroes/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

    @Test
    void testUpdateHeroWithDuplicateName() throws Exception {
        HeroRequestDTO updateRequest = new HeroRequestDTO(
            "Bruce Wayne",
            "Superman",
            LocalDate.of(1939, 3, 30),
            1.88,
            95.0,
                Collections.singletonList(superpoder1.getId())
        );

        mockMvc.perform(put("/api/heroes/{id}", hero2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Nome de herói duplicado"));
    }

    @Test
    void testDeleteHero() throws Exception {
        mockMvc.perform(delete("/api/heroes/{id}", hero2.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/heroes/{id}", hero2.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteHeroNotFound() throws Exception {
        mockMvc.perform(delete("/api/heroes/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

    @Test
    void testCreateHeroWithInvalidSuperpoderId() throws Exception {
        HeroRequestDTO invalidRequest = new HeroRequestDTO(
            "Peter Parker",
            "Homem-Aranha",
            LocalDate.of(1962, 8, 10),
            1.78,
            76.0,
                List.of(999L)
        );

        mockMvc.perform(post("/api/heroes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Superpoder não encontrado com ID: '999'"));
    }

    @Test
    void testGetAllHeroesEmpty() throws Exception {
        entityManager.createQuery("DELETE FROM Hero").executeUpdate();
        entityManager.flush();

        mockMvc.perform(get("/api/heroes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
