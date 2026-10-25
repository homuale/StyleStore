-- Crear la bd Stylestore_db
--Script definitivo
-- 1. ROLES
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. PERMISOS
CREATE TABLE permisos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. USUARIOS
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    rol VARCHAR(50) DEFAULT 'cliente',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. USUARIO_ROLES
CREATE TABLE usuario_roles (
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    rol_id INT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

-- 5. COMPLEJOS_DEPORTIVOS
CREATE TABLE complejos_deportivos (
    id SERIAL PRIMARY KEY,
    dueno_id INT REFERENCES usuarios(id) ON DELETE RESTRICT,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    latitud NUMERIC(10, 8),
    longitud NUMERIC(11, 8),
    politica_cancelacion TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. CARACTERISTICAS_ADICIONALES
CREATE TABLE caracteristicas_adicionales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- 7. COMPLEJO_CARACTERISTICAS
CREATE TABLE complejo_caracteristicas (
    complejo_id INT REFERENCES complejos_deportivos(id) ON DELETE CASCADE,
    caracteristica_id INT REFERENCES caracteristicas_adicionales(id) ON DELETE CASCADE,
    PRIMARY KEY (complejo_id, caracteristica_id)
);

-- 8. HORARIOS_ATENCION
CREATE TABLE horarios_atencion (
    id SERIAL PRIMARY KEY,
    complejo_id INT REFERENCES complejos_deportivos(id) ON DELETE CASCADE,
    dia_semana INT CHECK (dia_semana BETWEEN 1 AND 7), -- 1: Lunes, 7: Domingo
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL
);

-- 9. CANCHAS
CREATE TABLE canchas (
    id SERIAL PRIMARY KEY,
    complejo_id INT REFERENCES complejos_deportivos(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    tipo_superficie VARCHAR(50),
    formato INT, -- Ej: 5, 7, 11
    techada BOOLEAN DEFAULT FALSE,
    estado VARCHAR(30) DEFAULT 'disponible',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. MANTENIMIENTOS_CANCHA
CREATE TABLE mantenimientos_cancha (
    id SERIAL PRIMARY KEY,
    cancha_id INT REFERENCES canchas(id) ON DELETE CASCADE,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    motivo TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. TARIFAS_BASE
CREATE TABLE tarifas_base (
    id SERIAL PRIMARY KEY,
    cancha_id INT REFERENCES canchas(id) ON DELETE CASCADE,
    dia_semana INT CHECK (dia_semana BETWEEN 1 AND 7),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    precio NUMERIC(10, 2) NOT NULL
);

-- 12. TARIFAS_DINAMICAS
CREATE TABLE tarifas_dinamicas (
    id SERIAL PRIMARY KEY,
    cancha_id INT REFERENCES canchas(id) ON DELETE CASCADE,
    fecha_especifica DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    precio NUMERIC(10, 2) NOT NULL
);

-- 13. CUPONES_DESCUENTO
CREATE TABLE cupones_descuento (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    porcentaje_descuento NUMERIC(5, 2),
    monto_descuento NUMERIC(10, 2),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    limite_usos INT,
    usos_actuales INT DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE
);

-- 14. RESERVAS
CREATE TABLE reservas (
    id SERIAL PRIMARY KEY,
    cancha_id INT REFERENCES canchas(id) ON DELETE RESTRICT,
    usuario_id INT REFERENCES usuarios(id) ON DELETE RESTRICT,
    cupon_id INT REFERENCES cupones_descuento(id) ON DELETE SET NULL,
    fecha_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    precio_total NUMERIC(10, 2) NOT NULL,
    estado VARCHAR(30) DEFAULT 'pendiente',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_reserva_cancha_fecha_hora UNIQUE (cancha_id, fecha_reserva, hora_inicio)
);

-- 15. HISTORIAL_ESTADOS_RESERVAS
CREATE TABLE historial_estados_reservas (
    id SERIAL PRIMARY KEY,
    reserva_id INT REFERENCES reservas(id) ON DELETE CASCADE,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    cambiado_por INT REFERENCES usuarios(id) ON DELETE SET NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 16. PAGOS
CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    reserva_id INT REFERENCES reservas(id) ON DELETE CASCADE,
    monto NUMERIC(10, 2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado_pago VARCHAR(30) DEFAULT 'completado',
    transaccion_externa_id VARCHAR(150),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 17. PRODUCTOS_TIENDA
CREATE TABLE productos_tienda (
    id SERIAL PRIMARY KEY,
    complejo_id INT REFERENCES complejos_deportivos(id) ON DELETE CASCADE,
    nombre VARCHAR(150) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE
);

-- 18. VENTAS_TIENDA_DETALLE
CREATE TABLE ventas_tienda_detalle (
    id SERIAL PRIMARY KEY,
    reserva_id INT REFERENCES reservas(id) ON DELETE CASCADE,
    producto_id INT REFERENCES productos_tienda(id) ON DELETE RESTRICT,
    cantidad INT NOT NULL,
    precio_unitario NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 19. RESENAS
CREATE TABLE resenas (
    id SERIAL PRIMARY KEY,
    reserva_id INT REFERENCES reservas(id) ON DELETE CASCADE,
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    complejo_id INT REFERENCES complejos_deportivos(id) ON DELETE CASCADE,
    calificacion_general NUMERIC(3, 2),
    calificacion_cancha INT CHECK (calificacion_cancha BETWEEN 1 AND 5),
    calificacion_instalaciones INT CHECK (calificacion_instalaciones BETWEEN 1 AND 5),
    comentario TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 20. FAVORITOS_USUARIOS
CREATE TABLE favoritos_usuarios (
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    complejo_id INT REFERENCES complejos_deportivos(id) ON DELETE CASCADE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, complejo_id)
);