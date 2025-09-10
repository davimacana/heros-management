-- Script SQL para inserir dados iniciais
-- Superpoderes
INSERT INTO superpoderes (nome, descricao) VALUES
('Super Força', 'Capacidade de levantar objetos extremamente pesados'),
('Voo', 'Capacidade de voar pelos céus'),
('Visão de Calor', 'Capacidade de emitir raios de calor pelos olhos'),
('Super Velocidade', 'Capacidade de se mover em velocidades sobre-humanas'),
('Inteligência', 'Capacidade mental superior'),
('Artes Marciais', 'Habilidades avançadas de combate'),
('Agilidade', 'Capacidade de se mover com rapidez e precisão'),
('Sentido Aranha', 'Sexto sentido que alerta sobre perigos'),
('Braceletes Indestrutíveis', 'Braceletes que podem bloquear qualquer ataque'),
('Tecnologia', 'Conhecimento avançado em tecnologia');

-- Heróis
INSERT INTO herois (nome, nome_heroi, data_nascimento, altura, peso) VALUES
('Clark Kent', 'Superman', '1938-04-18', 1.91, 107.0),
('Bruce Wayne', 'Batman', '1939-03-30', 1.88, 95.0),
('Peter Parker', 'Homem-Aranha', '1962-08-10', 1.78, 76.0);

-- Relacionamentos Heróis-Superpoderes
-- Superman
INSERT INTO heroissuperpoderes (heroi_id, superpoder_id) VALUES
(1, 1), -- Super Força
(1, 2), -- Voo
(1, 3), -- Visão de Calor
(1, 4); -- Super Velocidade

-- Batman
INSERT INTO heroissuperpoderes (heroi_id, superpoder_id) VALUES
(2, 5), -- Inteligência
(2, 6), -- Artes Marciais
(2, 10); -- Tecnologia

-- Homem-Aranha
INSERT INTO heroissuperpoderes (heroi_id, superpoder_id) VALUES
(3, 1), -- Super Força
(3, 7), -- Agilidade
(3, 8); -- Sentido Aranha
