package com.heroes.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateHeroNameException extends RuntimeException {

    private String nomeHeroi;

    public DuplicateHeroNameException(String nomeHeroi) {
        super(String.format("Já existe um herói cadastrado com o nome '%s'", nomeHeroi));
        this.nomeHeroi = nomeHeroi;
    }

    public String getNomeHeroi() {
        return nomeHeroi;
    }
}
