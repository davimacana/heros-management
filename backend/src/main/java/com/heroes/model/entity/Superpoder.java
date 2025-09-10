package com.heroes.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "superpoderes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "heroes")
public class Superpoder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @ManyToMany(mappedBy = "superpoderes", fetch = FetchType.LAZY)
    private Set<Hero> heroes;

    public Superpoder(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
}
