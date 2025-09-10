package com.heroes.model.mapper;

import com.heroes.model.dto.HeroRequestDTO;
import com.heroes.model.dto.HeroResponseDTO;
import com.heroes.model.dto.SuperpoderDTO;
import com.heroes.model.entity.Hero;
import com.heroes.model.entity.Superpoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HeroMapper {

    public Hero toEntity(HeroRequestDTO dto, Set<Superpoder> superpoderes) {
        if (dto == null) {
            return null;
        }

        Hero hero = new Hero(
            dto.nome(),
            dto.nomeHeroi(),
            dto.dataNascimento(),
            dto.altura(),
            dto.peso()
        );
        hero.setSuperpoderes(superpoderes);
        return hero;
    }

    public HeroResponseDTO toResponseDTO(Hero hero) {
        if (hero == null) {
            return null;
        }

        Set<SuperpoderDTO> superpoderesDTO = hero.getSuperpoderes() != null 
            ? hero.getSuperpoderes().stream()
                .map(this::toSuperpoderDTO)
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

    public void updateEntityFromDTO(Hero hero, HeroRequestDTO dto, Set<Superpoder> superpoderes) {
        if (hero == null || dto == null) {
            return;
        }

        hero.setNome(dto.nome());
        hero.setNomeHeroi(dto.nomeHeroi());
        hero.setDataNascimento(dto.dataNascimento());
        hero.setAltura(dto.altura());
        hero.setPeso(dto.peso());
        hero.setSuperpoderes(superpoderes);
    }

    public SuperpoderDTO toSuperpoderDTO(Superpoder superpoder) {
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
