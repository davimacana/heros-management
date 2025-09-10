package com.heroes.controller;

import com.heroes.model.dto.SuperpoderDTO;
import com.heroes.service.SuperpoderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superpoderes")
@Tag(name = "Superpoderes", description = "API para listagem de superpoderes")
@CrossOrigin(origins = "http://localhost:4200")
public class SuperpoderController {

    private final SuperpoderService superpoderService;

    public SuperpoderController(SuperpoderService superpoderService) {
        this.superpoderService = superpoderService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os superpoderes", description = "Retorna uma lista com todos os superpoderes disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de superpoderes retornada com sucesso")
    public ResponseEntity<List<SuperpoderDTO>> getAllSuperpoderes() {
        return ResponseEntity.ok(superpoderService.findAll());
    }
}
