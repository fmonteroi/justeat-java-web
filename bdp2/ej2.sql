-- Añadir columna
ALTER TABLE restaurant
ADD COLUMN averageSpending INTEGER DEFAULT 0;

-- Actualizar datos residuales
UPDATE restaurant
SET averageSpending = (minPrice + maxPrice) / 2
WHERE averageSpending IS NULL OR averageSpending = 0;