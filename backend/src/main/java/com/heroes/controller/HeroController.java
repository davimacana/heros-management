package com.heroes.controller;

import com.heroes.model.dto.HeroRequestDTO;
import com.heroes.model.dto.HeroResponseDTO;
import com.heroes.service.HeroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/heroes")
@Tag(name = "Heroes", description = "API para gerenciamento de super-heróis")
@CrossOrigin(origins = "http://localhost:4200")
public class HeroController {

    private final HeroService heroService;

    public HeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os heróis", description = "Retorna uma lista com todos os super-heróis cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de heróis retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nenhum herói encontrado")
    })
    public ResponseEntity<List<HeroResponseDTO>> getAllHeroes() {
        return ResponseEntity.ok(heroService.findAllHeroes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar herói por ID", description = "Retorna um super-herói específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Herói encontrado"),
        @ApiResponse(responseCode = "404", description = "Herói não encontrado")
    })
    public ResponseEntity<HeroResponseDTO> getHeroById(
            @Parameter(description = "ID do herói") @PathVariable Long id) {
        return ResponseEntity.ok(heroService.findHeroById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo herói", description = "Cadastra um novo super-herói")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Herói criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Nome do herói já existe")
    })
    public ResponseEntity<HeroResponseDTO> createHero(@Valid @RequestBody HeroRequestDTO heroRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(heroService.createHero(heroRequestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar herói", description = "Atualiza as informações de um super-herói existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Herói atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Herói não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Nome do herói já existe")
    })
    public ResponseEntity<HeroResponseDTO> updateHero(
            @Parameter(description = "ID do herói") @PathVariable Long id,
            @Valid @RequestBody HeroRequestDTO heroRequestDTO) {
        return ResponseEntity.ok(heroService.updateHero(id, heroRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir herói", description = "Remove um super-herói do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Herói excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Herói não encontrado")
    })
    public ResponseEntity<Void> deleteHero(
            @Parameter(description = "ID do herói") @PathVariable Long id) {
        heroService.removeHero(id);
        return ResponseEntity.noContent().build();
    }
}
