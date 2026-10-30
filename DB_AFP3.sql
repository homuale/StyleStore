CREATE DATABASE IF NOT EXISTS stylestore CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE stylestore;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS categorias;
SET FOREIGN_KEY_CHECKS = 1;

-- Tablas
CREATE TABLE categorias (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(30)  NOT NULL,
  descripcion VARCHAR(150) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_categorias_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE usuarios (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  nombre     VARCHAR(60)  NOT NULL,
  correo     VARCHAR(100) NOT NULL,
  contrasena VARCHAR(100) NOT NULL,
  rol        VARCHAR(20)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_correo (correo),
  CONSTRAINT ck_usuarios_rol CHECK (rol IN ('ADMINISTRADOR','VENDEDOR'))
) ENGINE=InnoDB;

CREATE TABLE productos (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(80)   NOT NULL,
  precio       DECIMAL(10,2) NOT NULL,
  stock        INT           NOT NULL,
  talla        VARCHAR(10)   NOT NULL,
  imagen       VARCHAR(100)  NOT NULL,
  categoria_id BIGINT        NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id),
  CONSTRAINT ck_productos_precio CHECK (precio > 0),
  CONSTRAINT ck_productos_stock  CHECK (stock >= 0)
) ENGINE=InnoDB;

CREATE TABLE ventas (
  id         BIGINT        NOT NULL AUTO_INCREMENT,
  fecha      DATE          NOT NULL,
  total      DECIMAL(10,2) NOT NULL DEFAULT 0,
  usuario_id BIGINT        NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_ventas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
  id              BIGINT        NOT NULL AUTO_INCREMENT,
  venta_id        BIGINT        NOT NULL,
  producto_id     BIGINT        NOT NULL,
  cantidad        INT           NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal        DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_detalle_venta    FOREIGN KEY (venta_id)    REFERENCES ventas (id),
  CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos (id)
) ENGINE=InnoDB;

-- datos ejemplo
INSERT INTO categorias (id, nombre, descripcion) VALUES
 (1,'Polos y Blusas','Polos basicos, crop tops y blusas'),
 (2,'Pantalones','Pantalones y Jeans, Jeans y pantalones casuales'),
 (3,'Vestidos','Vestidos y Faldas, Vestidos casuales y faldas'),
 (4,'Casacas','Abrigos y Chaquetas, Casacas, chaquetas y cardigans'),
 (5,'Accesorios','Gorros, bufandas y más'),
 (6,'Camisas','Camisas Blanca para hombre');

INSERT INTO productos (id, nombre, precio, stock, talla, imagen, categoria_id) VALUES
 (1,'Polo Básico',59.90,10,'M','img1.jpeg',1),
 (2,'crop top',49.90,15,'M','img2.jpeg',1),
 (3,'Jean Recto',89.90,5,'32','img3.jpeg',2),
 (4,'Pantalón Largo',99.90,6,'32','img4.jpeg',2),
 (5,'Vestido Casual',129.90,8,'M','img5.jpeg',3),
 (6,'Falda Plisada',59.90,12,'M','img6.jpeg',3),
 (7,'Casaca denim',119.90,5,'L','img7.jpeg',4),
 (8,'Cardigan de lana',149.90,4,'L','img8.jpeg',4),
 (9,'Cartera de cuero pequeña',59.90,10,'Única','img9.jpeg',5),
 (10,'Gorro de Lana',39.90,24,'Única','img10.jpeg',5),
 (11,'Camisa Blanca',79.90,7,'M','img11.jpeg',6),
 (12,'Camisa de cuadros',89.90,5,'M','img12.jpeg',6);

INSERT INTO usuarios (id, nombre, correo, contrasena, rol) VALUES
 (1,'Administrador','admin@stylestore.com','admin123','ADMINISTRADOR'),
 (2,'Vendedor','vendedor1@stylestore.com','vendedor1','VENDEDOR'),
 (3,'Vendedor','vendedor2@stylestore.com','vendedor2','VENDEDOR');

INSERT INTO ventas (id, fecha, total, usuario_id) VALUES
 (1,'2026-09-01',209.70,2),
 (2,'2026-09-02',149.80,3),
 (3,'2026-09-03',249.80,2);

INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
 (1,1,2,59.90,119.80),
 (1,3,1,89.90,89.90),
 (2,2,1,49.90,49.90),
 (2,4,1,99.90,99.90),
 (3,5,1,129.90,129.90),
 (3,7,1,119.90,119.90);

-- consultas evidencias
SELECT * FROM categorias;
SELECT p.id, p.nombre, p.precio, p.stock, c.nombre AS categoria
  FROM productos p JOIN categorias c ON c.id = p.categoria_id;
SELECT v.id, v.fecha, u.nombre AS vendedor, v.total
  FROM ventas v JOIN usuarios u ON u.id = v.usuario_id;
SELECT d.venta_id, p.nombre, d.cantidad, d.precio_unitario, d.subtotal
  FROM detalle_venta d JOIN productos p ON p.id = d.producto_id ORDER BY d.venta_id;
