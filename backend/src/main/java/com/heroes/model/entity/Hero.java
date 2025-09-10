package com.heroes.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "herois")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "superpoderes")
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 120, unique = true)
    private String nomeHeroi;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private Double altura;

    @Column(nullable = false)
    private Double peso;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "heroissuperpoderes",
        joinColumns = @JoinColumn(name = "heroi_id"),
        inverseJoinColumns = @JoinColumn(name = "superpoder_id")
    )
    private Set<Superpoder> superpoderes;

    public Hero(String nome, String nomeHeroi, LocalDate dataNascimento, Double altura, Double peso) {
        this.nome = nome;
        this.nomeHeroi = nomeHeroi;
        this.dataNascimento = dataNascimento;
        this.altura = altura;
        this.peso = peso;
    }
}
