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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HeroService {

    private static final String HERO_RESOURCE_NAME = "Herói";
    private static final String SUPERPODER_RESOURCE_NAME = "Superpoder";

    private final HeroRepository heroRepository;
    private final SuperpoderRepository superpoderRepository;
    private final HeroMapper heroMapper;

    public HeroService(HeroRepository heroRepository,
                      SuperpoderRepository superpoderRepository,
                      HeroMapper heroMapper) {
        this.heroRepository = heroRepository;
        this.superpoderRepository = superpoderRepository;
        this.heroMapper = heroMapper;
    }

    @Transactional(readOnly = true)
    public List<HeroResponseDTO> findAllHeroes() {
        List<Hero> heroes = heroRepository.findAllWithSuperpoderes();
        return heroes.stream()
                .map(this::convertHeroToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HeroResponseDTO findHeroById(Long heroId) {
        Hero hero = findHeroByIdWithSuperpoderesOrThrow(heroId);
        return convertHeroToResponseDTO(hero);
    }

    @Transactional
    public HeroResponseDTO createHero(HeroRequestDTO heroRequest) {
        validateHeroNameUniqueness(heroRequest.nomeHeroi());

        Set<Superpoder> superpoderes = findSuperpoderesByIds(heroRequest.superpoderIds());

        Hero newHero = heroMapper.toEntity(heroRequest, superpoderes);
        Hero savedHero = heroRepository.save(newHero);

        Hero heroWithSuperpoderes = findHeroByIdWithSuperpoderesOrThrow(savedHero.getId());
        return convertHeroToResponseDTO(heroWithSuperpoderes);
    }

    @Transactional
    public HeroResponseDTO updateHero(Long heroId, HeroRequestDTO heroRequest) {
        Hero existingHero = findHeroByIdOrThrow(heroId);

        validateHeroNameUniquenessForUpdate(heroRequest.nomeHeroi(), heroId);

        Set<Superpoder> superpoderes = findSuperpoderesByIds(heroRequest.superpoderIds());

        heroMapper.updateEntityFromDTO(existingHero, heroRequest, superpoderes);
        Hero updatedHero = heroRepository.save(existingHero);

        Hero heroWithSuperpoderes = findHeroByIdWithSuperpoderesOrThrow(updatedHero.getId());
        return convertHeroToResponseDTO(heroWithSuperpoderes);
    }

    @Transactional
    public void removeHero(Long heroId) {
        validateHeroExists(heroId);
        heroRepository.deleteById(heroId);
    }


    private Hero findHeroByIdOrThrow(Long heroId) {
        return heroRepository.findById(heroId)
                .orElseThrow(() -> new ResourceNotFoundException(HERO_RESOURCE_NAME, "ID", heroId));
    }

    private Hero findHeroByIdWithSuperpoderesOrThrow(Long heroId) {
        return heroRepository.findByIdWithSuperpoderes(heroId)
                .orElseThrow(() -> new ResourceNotFoundException(HERO_RESOURCE_NAME, "ID", heroId));
    }

    private void validateHeroNameUniqueness(String heroName) {
        if (heroRepository.existsByNomeHeroi(heroName)) {
            throw new DuplicateHeroNameException(heroName);
        }
    }

    private void validateHeroNameUniquenessForUpdate(String heroName, Long heroId) {
        if (heroRepository.existsByNomeHeroiAndIdNot(heroName, heroId)) {
            throw new DuplicateHeroNameException(heroName);
        }
    }

    private void validateHeroExists(Long heroId) {
        if (!heroRepository.existsById(heroId)) {
            throw new ResourceNotFoundException(HERO_RESOURCE_NAME, "ID", heroId);
        }
    }

    private Set<Superpoder> findSuperpoderesByIds(List<Long> superpoderIds) {
        return superpoderIds.stream()
                .map(this::findSuperpoderByIdOrThrow)
                .collect(Collectors.toSet());
    }

    private Superpoder findSuperpoderByIdOrThrow(Long superpoderId) {
        return superpoderRepository.findById(superpoderId)
                .orElseThrow(() -> new ResourceNotFoundException(SUPERPODER_RESOURCE_NAME, "ID", superpoderId));
    }

    private HeroResponseDTO convertHeroToResponseDTO(Hero hero) {
        if (hero == null) {
            return null;
        }

        Set<Superpoder> superpoderes = hero.getSuperpoderes();
        Set<SuperpoderDTO> superpoderesDTO = superpoderes != null 
            ? superpoderes.stream()
                .map(this::convertSuperpoderToDTO)
                .collect(Collectors.toSet())
            : Set.of();

        return new HeroResponseDTO(
            hero.getId(),
            hero.getNome(),
            hero.getNomeHeroi(),
            hero.getDataNascimento(),
            hero.getAltura(),
            hero.getPeso(),
            superpoderesDTO.stream().toList()
        );
    }

    private SuperpoderDTO convertSuperpoderToDTO(Superpoder superpoder) {
        if (superpoder == null) {
            return null;
        }

        return new SuperpoderDTO(
            superpoder.getId(),
            superpoder.getNome(),
            superpoder.getDescricao()
        );
    }
}
