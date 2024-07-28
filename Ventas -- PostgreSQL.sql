-- PostgreSQL SQL Dump

BEGIN;

-- Base de datos: `sis_java`

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `clientes`
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    dni VARCHAR(8) NOT NULL,
    nombre VARCHAR(180) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    direccion VARCHAR(255) NOT NULL
);

-- Volcado de datos para la tabla `clientes`
INSERT INTO clientes (id, dni, nombre, telefono, direccion) VALUES
(1, '1234598', 'Angel sifuentes', '924878', 'Lima - Perú');

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `config`
CREATE TABLE config (
    id SERIAL PRIMARY KEY,
    ruc BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    telefono BIGINT NOT NULL,
    direccion TEXT NOT NULL,
    mensaje VARCHAR(255) NOT NULL
);

-- Volcado de datos para la tabla `config`
INSERT INTO config (id, ruc, nombre, telefono, direccion, mensaje) VALUES
(1, 71347267, 'Vida Informático', 925491523, 'Lima - Perú', 'Vida Informático');

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `detalle`
CREATE TABLE detalle (
    id SERIAL PRIMARY KEY,
    id_pro INT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    id_venta INT NOT NULL
);

-- Volcado de datos para la tabla `detalle`
INSERT INTO detalle (id, id_pro, cantidad, precio, id_venta) VALUES
(4, 1, 5, '3000.00', 4);

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `productos`
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL,
    nombre TEXT NOT NULL,
    proveedor INT NOT NULL,
    stock INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);

-- Volcado de datos para la tabla `productos`
INSERT INTO productos (id, codigo, nombre, proveedor, stock, precio) VALUES
(1, '79878789', 'Laptop lenovo', 1, 20, '3000.00');

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `proveedor`
CREATE TABLE proveedor (
    id SERIAL PRIMARY KEY,
    ruc VARCHAR(15) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    direccion VARCHAR(255) NOT NULL
);

-- Volcado de datos para la tabla `proveedor`
INSERT INTO proveedor (id, ruc, nombre, telefono, direccion) VALUES
(1, '998787', 'Open Services', '798978879', 'Lima - Perú');

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `usuarios`
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    correo VARCHAR(200) NOT NULL,
    pass VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

-- Volcado de datos para la tabla `usuarios`
INSERT INTO usuarios (id, nombre, correo, pass, rol) VALUES
(1, 'Angel Sifuentes', 'angel@gmail.com', 'admin', 'Administrador'),
(2, 'Vida Informatico', 'admin@gmail.com', 'admin', 'Administrador');

-- --------------------------------------------------------

-- Estructura de tabla para la tabla `ventas`
CREATE TABLE ventas (
    id SERIAL PRIMARY KEY,
    cliente INT NOT NULL,
    vendedor VARCHAR(60) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    fecha VARCHAR(20) NOT NULL
);

-- Volcado de datos para la tabla `ventas`
INSERT INTO ventas (id, cliente, vendedor, total, fecha) VALUES
(4, 1, 'Angel Sifuentes', '15000.00', '25/07/2021');

-- --------------------------------------------------------

-- Filtros para la tabla `detalle`
ALTER TABLE detalle
    ADD FOREIGN KEY (id_pro) REFERENCES productos(id) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD FOREIGN KEY (id_venta) REFERENCES ventas(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- Filtros para la tabla `productos`
ALTER TABLE productos
    ADD FOREIGN KEY (proveedor) REFERENCES proveedor(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- Filtros para la tabla `ventas`
ALTER TABLE ventas
    ADD FOREIGN KEY (cliente) REFERENCES clientes(id) ON DELETE CASCADE ON UPDATE CASCADE;

COMMIT;