-- Crear la bd Stylestore_db

--Tabla Categoria
CREATE TABLE Categoria (
    idCategoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

--Tabla Usuario
CREATE TABLE Usuario (
    idUsuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

--Tabla Producto (Relacionada con Categoria)
CREATE TABLE Producto (
    idProducto SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    talla VARCHAR(20),
    idCategoria INT NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (idCategoria)
        REFERENCES Categoria (idCategoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

--Tabla Venta (Relacionada con Usuario)
CREATE TABLE Venta (
    idVenta SERIAL PRIMARY KEY,
    fecha TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10, 2) NOT NULL,
    idUsuario INT NOT NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (idUsuario)
        REFERENCES Usuario (idUsuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

--Tabla DetalleVenta (Relacionada con Venta y Producto)
CREATE TABLE DetalleVenta (
    idDetalle SERIAL PRIMARY KEY,
    idVenta INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (idVenta)
        REFERENCES Venta (idVenta)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (idProducto)
        REFERENCES Producto (idProducto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);