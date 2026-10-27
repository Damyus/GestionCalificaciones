CREATE DATABASE IF NOT EXISTS calificaciones_db;
USE calificaciones_db;

CREATE TABLE IF NOT EXISTS calificaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante VARCHAR(100) NOT NULL,
    materia VARCHAR(100) NOT NULL,
    nota DOUBLE NOT NULL,
    fecha DATE,
    CHECK (nota >= 0 AND nota <= 10)
);

-- datos de prueba--
INSERT INTO calificaciones (estudiante, materia, nota, fecha) VALUES
('Mauro Cachuka', 'Matematica', 8, '2026-08-10'),
('Mauro Cachuka', 'Matematica', 6.5, '2026-09-15'),
('Mauro Cachuka', 'Lengua', 9, '2026-08-20'),
('Mauro Cachuka', 'Historia', 7, '2026-09-01'),
('Ernesto Pereira', 'Matematica', 10, '2026-08-10'),
('Ernesto Pereira', 'Lengua', 7.5, '2026-08-20'),
('Ernesto Pereira', 'Historia', 8, '2026-09-01'),
('Ana Montenegro', 'Matematica', 4, '2026-08-10'),
('Ana Montenegro', 'Lengua', 6, '2026-08-20'),
('Carmen Milla', 'Historia', 5.5, '2026-09-01'),
('Lucia Canda', 'Matematica', 9.5, '2026-09-15'),
('Lucia Canda', 'Ingles', 10, '2026-09-22'),
('Lucia Canda', 'Lengua', 10, "2026-09-23");

-- consultas--
SELECT * FROM calificaciones ORDER BY estudiante, materia;

-- promedio por estudiante--
SELECT estudiante, ROUND(AVG(nota), 2) AS promedio
FROM calificaciones
GROUP BY estudiante;

-- promedio por materia--
SELECT materia, ROUND(AVG(nota), 2) AS promedio
FROM calificaciones
GROUP BY materia;
