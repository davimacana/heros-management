package com.heroes.service;

import com.heroes.model.dto.SuperpoderDTO;
import com.heroes.model.entity.Superpoder;
import com.heroes.model.mapper.HeroMapper;
import com.heroes.repository.SuperpoderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SuperpoderService {

    private final SuperpoderRepository superpoderRepository;
    private final HeroMapper heroMapper;

    public SuperpoderService(SuperpoderRepository superpoderRepository, HeroMapper heroMapper) {
        this.superpoderRepository = superpoderRepository;
        this.heroMapper = heroMapper;
    }

    public List<SuperpoderDTO> findAll() {
        return superpoderRepository.findAll().stream()
                .map(heroMapper::toSuperpoderDTO)
                .collect(Collectors.toList());
    }
}
