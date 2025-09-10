package com.heroes.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

public record HeroRequestDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,
    
    @NotBlank(message = "Nome do herói é obrigatório")
    @Size(max = 120, message = "Nome do herói deve ter no máximo 120 caracteres")
    String nomeHeroi,
    
    @NotNull(message = "Data de nascimento é obrigatória")
    LocalDate dataNascimento,
    
    @NotNull(message = "Altura é obrigatória")
    @Positive(message = "Altura deve ser um valor positivo")
    Double altura,
    
    @NotNull(message = "Peso é obrigatório")
    @Positive(message = "Peso deve ser um valor positivo")
    Double peso,
    
    @NotNull(message = "Lista de superpoderes é obrigatória")
    @Size(min = 1, message = "Deve ser selecionado pelo menos um superpoder")
    List<Long> superpoderIds
) {}
