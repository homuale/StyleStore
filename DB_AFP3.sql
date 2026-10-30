CREATE DATABASE IF NOT EXISTS stylestore CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE stylestore;
SHOW DATABASES;
SELECT * FROM categorias;
SELECT p.id, p.nombre, p.precio, p.stock, c.nombre AS categoria
  FROM productos p JOIN categorias c ON c.id = p.categoria_id;
SELECT v.id, v.fecha, u.nombre AS vendedor, v.total
  FROM ventas v JOIN usuarios u ON u.id = v.usuario_id;
SELECT d.venta_id, p.nombre, d.cantidad, d.precio_unitario, d.subtotal
  FROM detalle_venta d JOIN productos p ON p.id = d.producto_id ORDER BY d.venta_id;