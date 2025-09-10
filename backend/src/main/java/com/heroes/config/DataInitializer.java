package com.heroes.config;

import com.heroes.model.entity.Hero;
import com.heroes.model.entity.Superpoder;
import com.heroes.repository.HeroRepository;
import com.heroes.repository.SuperpoderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HeroRepository heroRepository;
    private final SuperpoderRepository superpoderRepository;

    public DataInitializer(HeroRepository heroRepository, SuperpoderRepository superpoderRepository) {
        this.heroRepository = heroRepository;
        this.superpoderRepository = superpoderRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (superpoderRepository.count() == 0) {
            createSuperpoderes();
        }

        if (heroRepository.count() == 0) {
            createHeroes();
        }
    }

    private void createSuperpoderes() {
        Superpoder superForca = new Superpoder("Super Força", "Capacidade de levantar objetos extremamente pesados");
        Superpoder voo = new Superpoder("Voo", "Capacidade de voar pelos céus");
        Superpoder visaoCalor = new Superpoder("Visão de Calor", "Capacidade de emitir raios de calor pelos olhos");
        Superpoder superVelocidade = new Superpoder("Super Velocidade", "Capacidade de se mover em velocidades sobre-humanas");
        Superpoder inteligencia = new Superpoder("Inteligência", "Capacidade mental superior");
        Superpoder artesMarciais = new Superpoder("Artes Marciais", "Habilidades avançadas de combate");
        Superpoder agilidade = new Superpoder("Agilidade", "Capacidade de se mover com rapidez e precisão");
        Superpoder sentidoAranha = new Superpoder("Sentido Aranha", "Sexto sentido que alerta sobre perigos");
        Superpoder braceletesIndestrutiveis = new Superpoder("Braceletes Indestrutíveis", "Braceletes que podem bloquear qualquer ataque");
        Superpoder tecnologia = new Superpoder("Tecnologia", "Conhecimento avançado em tecnologia");

        superpoderRepository.save(superForca);
        superpoderRepository.save(voo);
        superpoderRepository.save(visaoCalor);
        superpoderRepository.save(superVelocidade);
        superpoderRepository.save(inteligencia);
        superpoderRepository.save(artesMarciais);
        superpoderRepository.save(agilidade);
        superpoderRepository.save(sentidoAranha);
        superpoderRepository.save(braceletesIndestrutiveis);
        superpoderRepository.save(tecnologia);

        System.out.println("Superpoderes iniciais criados com sucesso!");
    }

    private void createHeroes() {
        Superpoder superForca = superpoderRepository.findByNome("Super Força").orElseThrow();
        Superpoder voo = superpoderRepository.findByNome("Voo").orElseThrow();
        Superpoder visaoCalor = superpoderRepository.findByNome("Visão de Calor").orElseThrow();
        Superpoder superVelocidade = superpoderRepository.findByNome("Super Velocidade").orElseThrow();
        Superpoder inteligencia = superpoderRepository.findByNome("Inteligência").orElseThrow();
        Superpoder artesMarciais = superpoderRepository.findByNome("Artes Marciais").orElseThrow();
        Superpoder agilidade = superpoderRepository.findByNome("Agilidade").orElseThrow();
        Superpoder sentidoAranha = superpoderRepository.findByNome("Sentido Aranha").orElseThrow();
        Superpoder tecnologia = superpoderRepository.findByNome("Tecnologia").orElseThrow();

        Hero superman = new Hero("Clark Kent", "Superman", LocalDate.of(1938, 4, 18), 1.91, 107.0);
        Set<Superpoder> supermanPowers = new HashSet<>();
        supermanPowers.add(superForca);
        supermanPowers.add(voo);
        supermanPowers.add(visaoCalor);
        supermanPowers.add(superVelocidade);
        superman.setSuperpoderes(supermanPowers);
        heroRepository.save(superman);

        Hero batman = new Hero("Bruce Wayne", "Batman", LocalDate.of(1939, 3, 30), 1.88, 95.0);
        Set<Superpoder> batmanPowers = new HashSet<>();
        batmanPowers.add(inteligencia);
        batmanPowers.add(artesMarciais);
        batmanPowers.add(tecnologia);
        batman.setSuperpoderes(batmanPowers);
        heroRepository.save(batman);

        Hero spiderman = new Hero("Peter Parker", "Homem-Aranha", LocalDate.of(1962, 8, 10), 1.78, 76.0);
        Set<Superpoder> spidermanPowers = new HashSet<>();
        spidermanPowers.add(superForca);
        spidermanPowers.add(agilidade);
        spidermanPowers.add(sentidoAranha);
        spiderman.setSuperpoderes(spidermanPowers);
        heroRepository.save(spiderman);

        System.out.println("Heróis iniciais criados com sucesso!");
    }

}
