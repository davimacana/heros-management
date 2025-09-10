package com.heroes.controller;

import com.heroes.HeroesManagementApplication;
import com.heroes.config.TestConfig;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HeroesManagementApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class SuperpoderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;


    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;

    private Superpoder superpoder1;
    private Superpoder superpoder2;
    private Superpoder superpoder3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

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
    void testGetAllSuperpoderes() throws Exception {
        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].nome").value("Super Força"))
                .andExpect(jsonPath("$[0].descricao").value("Capacidade de levantar objetos extremamente pesados"))
                .andExpect(jsonPath("$[1].nome").value("Voo"))
                .andExpect(jsonPath("$[1].descricao").value("Capacidade de voar pelos céus"))
                .andExpect(jsonPath("$[2].nome").value("Visão de Calor"))
                .andExpect(jsonPath("$[2].descricao").value("Capacidade de emitir raios de calor pelos olhos"));
    }

    @Test
    void testGetAllSuperpoderesEmpty() throws Exception {
        entityManager.createQuery("DELETE FROM Superpoder").executeUpdate();
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllSuperpoderesWithSingleSuperpoder() throws Exception {
        entityManager.createQuery("DELETE FROM Superpoder WHERE id != :id")
                .setParameter("id", superpoder1.getId())
                .executeUpdate();
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Super Força"))
                .andExpect(jsonPath("$[0].descricao").value("Capacidade de levantar objetos extremamente pesados"));
    }

    @Test
    void testGetAllSuperpoderesWithLargeDataset() throws Exception {
        List<Superpoder> additionalSuperpoderes = Arrays.asList(
            new Superpoder("Super Velocidade", "Capacidade de se mover em velocidades sobre-humanas"),
            new Superpoder("Inteligência", "Capacidade mental superior"),
            new Superpoder("Artes Marciais", "Habilidades avançadas de combate"),
            new Superpoder("Agilidade", "Capacidade de se mover com rapidez e precisão"),
            new Superpoder("Sentido Aranha", "Sexto sentido que alerta sobre perigos"),
            new Superpoder("Braceletes Indestrutíveis", "Braceletes que podem bloquear qualquer ataque"),
            new Superpoder("Tecnologia", "Conhecimento avançado em tecnologia")
        );

        for (Superpoder superpoder : additionalSuperpoderes) {
            entityManager.persist(superpoder);
        }
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void testGetAllSuperpoderesWithNullDescription() throws Exception {
        Superpoder superpoderWithNullDesc = new Superpoder("Superpoder Sem Descrição", null);
        entityManager.persist(superpoderWithNullDesc);
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[3].nome").value("Superpoder Sem Descrição"))
                .andExpect(jsonPath("$[3].descricao").isEmpty());
    }

    @Test
    void testGetAllSuperpoderesWithEmptyDescription() throws Exception {
        Superpoder superpoderWithEmptyDesc = new Superpoder("Superpoder Descrição Vazia", "");
        entityManager.persist(superpoderWithEmptyDesc);
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[3].nome").value("Superpoder Descrição Vazia"))
                .andExpect(jsonPath("$[3].descricao").value(""));
    }

    @Test
    void testGetAllSuperpoderesWithLongDescription() throws Exception {
        String longDescription = "Esta é uma descrição muito longa para testar como o sistema lida com textos extensos. " +
                "A descrição deve ser armazenada corretamente mesmo quando contém muitos caracteres. " +
                "Este teste verifica se não há limitações indevidas no tamanho da descrição. " +
                "A descrição pode conter até 500 caracteres conforme definido na entidade.";

        Superpoder superpoderWithLongDesc = new Superpoder("Superpoder Descrição Longa", longDescription);
        entityManager.persist(superpoderWithLongDesc);
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[3].nome").value("Superpoder Descrição Longa"))
                .andExpect(jsonPath("$[3].descricao").value(longDescription));
    }

    @Test
    void testGetAllSuperpoderesWithSpecialCharacters() throws Exception {
        Superpoder superpoderWithSpecialChars = new Superpoder(
            "Superpoder Especial & Único",
            "Descrição com caracteres especiais: áéíóú, ç, ñ, @#$%&*()"
        );
        entityManager.persist(superpoderWithSpecialChars);
        entityManager.flush();

        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[3].nome").value("Superpoder Especial & Único"))
                .andExpect(jsonPath("$[3].descricao").value("Descrição com caracteres especiais: áéíóú, ç, ñ, @#$%&*()"));
    }

    @Test
    void testGetAllSuperpoderesResponseStructure() throws Exception {
        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].nome").exists())
                .andExpect(jsonPath("$[0].descricao").exists())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].nome").isString())
                .andExpect(jsonPath("$[0].descricao").isString());
    }

    @Test
    void testGetAllSuperpoderesOrder() throws Exception {
        mockMvc.perform(get("/api/superpoderes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(superpoder1.getId()))
                .andExpect(jsonPath("$[1].id").value(superpoder2.getId()))
                .andExpect(jsonPath("$[2].id").value(superpoder3.getId()));
    }
}
