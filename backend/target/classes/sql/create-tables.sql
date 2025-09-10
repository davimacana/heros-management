-- Script SQL para criação das tabelas do sistema de heróis
-- Compatível com SQL Server, MySQL, PostgreSQL

-- Tabela de Superpoderes
CREATE TABLE superpoderes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome NVARCHAR(120) NOT NULL UNIQUE,
    descricao NVARCHAR(500)
);

-- Tabela de Heróis
CREATE TABLE herois (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome NVARCHAR(120) NOT NULL,
    nome_heroi NVARCHAR(120) NOT NULL UNIQUE,
    data_nascimento DATETIME2(7) NOT NULL,
    altura FLOAT NOT NULL,
    peso FLOAT NOT NULL
);

-- Tabela de relacionamento Heróis-Superpoderes
CREATE TABLE heroissuperpoderes (
    heroi_id BIGINT NOT NULL,
    superpoder_id BIGINT NOT NULL,
    PRIMARY KEY (heroi_id, superpoder_id),
    FOREIGN KEY (heroi_id) REFERENCES herois(id) ON DELETE CASCADE,
    FOREIGN KEY (superpoder_id) REFERENCES superpoderes(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_herois_nome_heroi ON herois(nome_heroi);
CREATE INDEX idx_superpoderes_nome ON superpoderes(nome);
CREATE INDEX idx_heroissuperpoderes_heroi ON heroissuperpoderes(heroi_id);
CREATE INDEX idx_heroissuperpoderes_superpoder ON heroissuperpoderes(superpoder_id);
