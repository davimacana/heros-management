package com.heroes.model.dto;

import java.time.LocalDate;
import java.util.List;

public record HeroResponseDTO(
    Long id,
    String nome,
    String nomeHeroi,
    LocalDate dataNascimento,
    Double altura,
    Double peso,
    List<SuperpoderDTO> superpoderes
) {}