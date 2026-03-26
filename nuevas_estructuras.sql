-- =====================================================
-- SEGUNDO AVANCE - ESTRUCTURAS FALTANTES
-- Proyecto: TiquiciaTech Inventario
-- Estructuras: Vistas, Funciones, Paquetes, Triggers, Cursores
-- =====================================================


-- =====================================================
-- VISTAS (10 vistas)
-- =====================================================

-- 1. Vista general de productos con categoria y proveedor
CREATE OR REPLACE VIEW vw_productos_completos AS
    SELECT p.id_producto,
           p.sku,
           p.nombre                  AS producto,
           p.descripcion,
           p.precio,
           p.estado,
           p.fecha_creacion,
           c.nombre                  AS categoria,
           pr.nombre                 AS proveedor,
           pr.email                  AS email_proveedor
    FROM PRODUCTO p
    JOIN CATEGORIA  c  ON c.id_categoria = p.id_categoria
    JOIN PROVEEDOR  pr ON pr.id_proveedor = p.id_proveedor;
/

-- 2. Vista de inventario con nombre de producto
CREATE OR REPLACE VIEW vw_inventario_detallado AS
    SELECT i.id_inventario,
           p.sku,
           p.nombre                  AS producto,
           i.stock_actual,
           i.stock_minimo,
           i.stock_maximo,
           CASE
               WHEN i.stock_actual <= i.stock_minimo THEN 'BAJO'
               WHEN i.stock_maximo IS NOT NULL AND i.stock_actual >= i.stock_maximo THEN 'ALTO'
               ELSE 'NORMAL'
           END                       AS estado_stock
    FROM INVENTARIO i
    JOIN PRODUCTO p ON p.id_producto = i.id_producto;
/

-- 3. Vista de productos con stock bajo
CREATE OR REPLACE VIEW vw_productos_stock_bajo AS
    SELECT p.id_producto,
           p.sku,
           p.nombre                  AS producto,
           i.stock_actual,
           i.stock_minimo
    FROM INVENTARIO i
    JOIN PRODUCTO p ON p.id_producto = i.id_producto
    WHERE i.stock_actual <= i.stock_minimo;
/

-- 4. Vista de movimientos con nombre de usuario
CREATE OR REPLACE VIEW vw_movimientos_completos AS
    SELECT m.id_movimiento,
           u.nombre                  AS usuario,
           u.email                   AS email_usuario,
           m.tipo,
           m.fecha,
           m.observacion
    FROM MOVIMIENTO m
    JOIN USUARIO u ON u.id_usuario = m.id_usuario;
/

-- 5. Vista del detalle de movimientos con producto
CREATE OR REPLACE VIEW vw_detalle_movimientos AS
    SELECT md.id_detalle,
           m.id_movimiento,
           m.tipo                    AS tipo_movimiento,
           m.fecha,
           p.sku,
           p.nombre                  AS producto,
           md.cantidad,
           md.costo_unitario,
           (md.cantidad * md.costo_unitario) AS subtotal
    FROM MOVIMIENTO_DETALLE md
    JOIN MOVIMIENTO m ON m.id_movimiento = md.id_movimiento
    JOIN PRODUCTO   p ON p.id_producto   = md.id_producto;
/

-- 6. Vista de usuarios con su rol
CREATE OR REPLACE VIEW vw_usuarios_roles AS
    SELECT u.id_usuario,
           u.nombre,
           u.email,
           u.estado,
           u.fecha_creacion,
           r.nombre AS rol
    FROM USUARIO u
    JOIN ROL r ON r.id_rol = u.id_rol;
/

-- 7. Vista resumen de movimientos por tipo
CREATE OR REPLACE VIEW vw_resumen_movimientos AS
    SELECT m.tipo,
           COUNT(m.id_movimiento)        AS total_movimientos,
           SUM(md.cantidad)              AS total_unidades,
           SUM(md.cantidad * md.costo_unitario) AS valor_total
    FROM MOVIMIENTO m
    JOIN MOVIMIENTO_DETALLE md ON md.id_movimiento = m.id_movimiento
    GROUP BY m.tipo;
/

-- 8. Vista de productos activos por categoria
CREATE OR REPLACE VIEW vw_productos_por_categoria AS
    SELECT c.id_categoria,
           c.nombre                  AS categoria,
           COUNT(p.id_producto)      AS total_productos,
           SUM(CASE WHEN p.estado = 'ACTIVO' THEN 1 ELSE 0 END) AS productos_activos
    FROM CATEGORIA c
    LEFT JOIN PRODUCTO p ON p.id_categoria = c.id_categoria
    GROUP BY c.id_categoria, c.nombre;
/

-- 9. Vista de valor del inventario por producto
CREATE OR REPLACE VIEW vw_valor_inventario AS
    SELECT p.id_producto,
           p.sku,
           p.nombre                      AS producto,
           i.stock_actual,
           p.precio,
           (i.stock_actual * p.precio)   AS valor_total
    FROM PRODUCTO p
    JOIN INVENTARIO i ON i.id_producto = p.id_producto
    WHERE p.estado = 'ACTIVO';
/

-- 10. Vista de proveedores con cantidad de productos
CREATE OR REPLACE VIEW vw_proveedores_productos AS
    SELECT pr.id_proveedor,
           pr.nombre                 AS proveedor,
           pr.email,
           pr.telefono,
           pr.estado,
           COUNT(p.id_producto)      AS total_productos
    FROM PROVEEDOR pr
    LEFT JOIN PRODUCTO p ON p.id_proveedor = pr.id_proveedor
    GROUP BY pr.id_proveedor, pr.nombre, pr.email, pr.telefono, pr.estado;
/


-- =====================================================
-- FUNCIONES (15 funciones)
-- =====================================================

-- 1. Obtener el stock actual de un producto
CREATE OR REPLACE FUNCTION fn_stock_actual (
    p_id_producto IN NUMBER
) RETURN NUMBER
AS
    v_stock NUMBER;
BEGIN
    SELECT stock_actual INTO v_stock
    FROM INVENTARIO
    WHERE id_producto = p_id_producto;
    RETURN v_stock;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN -1;
END;
/

-- 2. Verificar si un producto tiene stock bajo
CREATE OR REPLACE FUNCTION fn_es_stock_bajo (
    p_id_producto IN NUMBER
) RETURN VARCHAR2
AS
    v_actual  NUMBER;
    v_minimo  NUMBER;
BEGIN
    SELECT stock_actual, stock_minimo
    INTO v_actual, v_minimo
    FROM INVENTARIO
    WHERE id_producto = p_id_producto;

    IF v_actual <= v_minimo THEN
        RETURN 'SI';
    ELSE
        RETURN 'NO';
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 'NO_EXISTE';
END;
/

-- 3. Calcular el valor total del inventario de un producto
CREATE OR REPLACE FUNCTION fn_valor_producto (
    p_id_producto IN NUMBER
) RETURN NUMBER
AS
    v_stock  NUMBER;
    v_precio NUMBER;
BEGIN
    SELECT i.stock_actual, p.precio
    INTO v_stock, v_precio
    FROM INVENTARIO i
    JOIN PRODUCTO p ON p.id_producto = i.id_producto
    WHERE i.id_producto = p_id_producto;
    RETURN v_stock * v_precio;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 0;
END;
/

-- 4. Obtener el nombre de un producto por ID
CREATE OR REPLACE FUNCTION fn_nombre_producto (
    p_id_producto IN NUMBER
) RETURN VARCHAR2
AS
    v_nombre VARCHAR2(120);
BEGIN
    SELECT nombre INTO v_nombre FROM PRODUCTO WHERE id_producto = p_id_producto;
    RETURN v_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 'NO_ENCONTRADO';
END;
/

-- 5. Obtener el nombre de una categoria por ID
CREATE OR REPLACE FUNCTION fn_nombre_categoria (
    p_id_categoria IN NUMBER
) RETURN VARCHAR2
AS
    v_nombre VARCHAR2(80);
BEGIN
    SELECT nombre INTO v_nombre FROM CATEGORIA WHERE id_categoria = p_id_categoria;
    RETURN v_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 'NO_ENCONTRADO';
END;
/

-- 6. Contar productos de una categoria
CREATE OR REPLACE FUNCTION fn_contar_productos_categoria (
    p_id_categoria IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_total FROM PRODUCTO WHERE id_categoria = p_id_categoria;
    RETURN v_total;
END;
/

-- 7. Calcular el valor total del inventario completo
CREATE OR REPLACE FUNCTION fn_valor_total_inventario
RETURN NUMBER
AS
    v_total NUMBER := 0;
BEGIN
    SELECT SUM(i.stock_actual * p.precio)
    INTO v_total
    FROM INVENTARIO i
    JOIN PRODUCTO p ON p.id_producto = i.id_producto
    WHERE p.estado = 'ACTIVO';
    RETURN NVL(v_total, 0);
END;
/

-- 8. Verificar si existe un SKU
CREATE OR REPLACE FUNCTION fn_existe_sku (
    p_sku IN VARCHAR2
) RETURN VARCHAR2
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM PRODUCTO WHERE sku = p_sku;
    IF v_count > 0 THEN RETURN 'SI'; ELSE RETURN 'NO'; END IF;
END;
/

-- 9. Obtener el total de movimientos de un usuario
CREATE OR REPLACE FUNCTION fn_total_movimientos_usuario (
    p_id_usuario IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_total FROM MOVIMIENTO WHERE id_usuario = p_id_usuario;
    RETURN v_total;
END;
/

-- 10. Calcular el subtotal de un movimiento
CREATE OR REPLACE FUNCTION fn_subtotal_movimiento (
    p_id_movimiento IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER;
BEGIN
    SELECT SUM(cantidad * costo_unitario)
    INTO v_total
    FROM MOVIMIENTO_DETALLE
    WHERE id_movimiento = p_id_movimiento;
    RETURN NVL(v_total, 0);
END;
/

-- 11. Obtener el nombre del proveedor de un producto
CREATE OR REPLACE FUNCTION fn_proveedor_de_producto (
    p_id_producto IN NUMBER
) RETURN VARCHAR2
AS
    v_nombre VARCHAR2(120);
BEGIN
    SELECT pr.nombre
    INTO v_nombre
    FROM PRODUCTO p
    JOIN PROVEEDOR pr ON pr.id_proveedor = p.id_proveedor
    WHERE p.id_producto = p_id_producto;
    RETURN v_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 'NO_ENCONTRADO';
END;
/

-- 12. Contar productos con stock bajo
CREATE OR REPLACE FUNCTION fn_contar_stock_bajo
RETURN NUMBER
AS
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_total
    FROM INVENTARIO
    WHERE stock_actual <= stock_minimo;
    RETURN v_total;
END;
/

-- 13. Verificar si un usuario existe por email
CREATE OR REPLACE FUNCTION fn_usuario_existe_email (
    p_email IN VARCHAR2
) RETURN VARCHAR2
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM USUARIO WHERE email = p_email;
    IF v_count > 0 THEN RETURN 'SI'; ELSE RETURN 'NO'; END IF;
END;
/

-- 14. Obtener el estado de un proveedor
CREATE OR REPLACE FUNCTION fn_estado_proveedor (
    p_id_proveedor IN NUMBER
) RETURN VARCHAR2
AS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado FROM PROVEEDOR WHERE id_proveedor = p_id_proveedor;
    RETURN v_estado;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 'NO_EXISTE';
END;
/

-- 15. Calcular cantidad total vendida de un producto (salidas)
CREATE OR REPLACE FUNCTION fn_total_salidas_producto (
    p_id_producto IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER;
BEGIN
    SELECT SUM(md.cantidad)
    INTO v_total
    FROM MOVIMIENTO_DETALLE md
    JOIN MOVIMIENTO m ON m.id_movimiento = md.id_movimiento
    WHERE md.id_producto = p_id_producto
      AND m.tipo = 'SALIDA';
    RETURN NVL(v_total, 0);
END;
/


-- =====================================================
-- PAQUETES (10 paquetes)
-- =====================================================

-- Paquete 1: Gestion de Categorias
CREATE OR REPLACE PACKAGE pkg_categoria AS
    PROCEDURE insertar(p_nombre VARCHAR2, p_descripcion VARCHAR2, p_estado VARCHAR2);
    PROCEDURE actualizar(p_id NUMBER, p_nombre VARCHAR2, p_descripcion VARCHAR2, p_estado VARCHAR2);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  contar RETURN NUMBER;
END pkg_categoria;
/

CREATE OR REPLACE PACKAGE BODY pkg_categoria AS
    PROCEDURE insertar(p_nombre VARCHAR2, p_descripcion VARCHAR2, p_estado VARCHAR2) IS
    BEGIN
        INSERT INTO CATEGORIA (nombre, descripcion, estado) VALUES (p_nombre, p_descripcion, p_estado);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_nombre VARCHAR2, p_descripcion VARCHAR2, p_estado VARCHAR2) IS
    BEGIN
        UPDATE CATEGORIA SET nombre=p_nombre, descripcion=p_descripcion, estado=p_estado WHERE id_categoria=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM CATEGORIA WHERE id_categoria = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_categoria, nombre, descripcion, estado FROM CATEGORIA ORDER BY id_categoria;
    END;

    FUNCTION contar RETURN NUMBER IS
        v_n NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_n FROM CATEGORIA;
        RETURN v_n;
    END;
END pkg_categoria;
/

-- Paquete 2: Gestion de Proveedores
CREATE OR REPLACE PACKAGE pkg_proveedor AS
    PROCEDURE insertar(p_nombre VARCHAR2, p_telefono VARCHAR2, p_email VARCHAR2, p_direccion VARCHAR2, p_estado VARCHAR2);
    PROCEDURE actualizar(p_id NUMBER, p_nombre VARCHAR2, p_telefono VARCHAR2, p_email VARCHAR2, p_direccion VARCHAR2, p_estado VARCHAR2);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  contar RETURN NUMBER;
END pkg_proveedor;
/

CREATE OR REPLACE PACKAGE BODY pkg_proveedor AS
    PROCEDURE insertar(p_nombre VARCHAR2, p_telefono VARCHAR2, p_email VARCHAR2, p_direccion VARCHAR2, p_estado VARCHAR2) IS
    BEGIN
        INSERT INTO PROVEEDOR (nombre, telefono, email, direccion, estado) VALUES (p_nombre, p_telefono, p_email, p_direccion, p_estado);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_nombre VARCHAR2, p_telefono VARCHAR2, p_email VARCHAR2, p_direccion VARCHAR2, p_estado VARCHAR2) IS
    BEGIN
        UPDATE PROVEEDOR SET nombre=p_nombre, telefono=p_telefono, email=p_email, direccion=p_direccion, estado=p_estado WHERE id_proveedor=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM PROVEEDOR WHERE id_proveedor = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_proveedor, nombre, telefono, email, direccion, estado FROM PROVEEDOR ORDER BY id_proveedor;
    END;

    FUNCTION contar RETURN NUMBER IS
        v_n NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_n FROM PROVEEDOR;
        RETURN v_n;
    END;
END pkg_proveedor;
/

-- Paquete 3: Gestion de Productos
CREATE OR REPLACE PACKAGE pkg_producto AS
    PROCEDURE insertar(p_id_cat NUMBER, p_id_prov NUMBER, p_sku VARCHAR2, p_nombre VARCHAR2, p_desc VARCHAR2, p_precio NUMBER, p_estado VARCHAR2);
    PROCEDURE actualizar(p_id NUMBER, p_id_cat NUMBER, p_id_prov NUMBER, p_sku VARCHAR2, p_nombre VARCHAR2, p_desc VARCHAR2, p_precio NUMBER, p_estado VARCHAR2);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  contar_activos RETURN NUMBER;
END pkg_producto;
/

CREATE OR REPLACE PACKAGE BODY pkg_producto AS
    PROCEDURE insertar(p_id_cat NUMBER, p_id_prov NUMBER, p_sku VARCHAR2, p_nombre VARCHAR2, p_desc VARCHAR2, p_precio NUMBER, p_estado VARCHAR2) IS
    BEGIN
        INSERT INTO PRODUCTO (id_categoria, id_proveedor, sku, nombre, descripcion, precio, estado)
        VALUES (p_id_cat, p_id_prov, p_sku, p_nombre, p_desc, p_precio, p_estado);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_id_cat NUMBER, p_id_prov NUMBER, p_sku VARCHAR2, p_nombre VARCHAR2, p_desc VARCHAR2, p_precio NUMBER, p_estado VARCHAR2) IS
    BEGIN
        UPDATE PRODUCTO SET id_categoria=p_id_cat, id_proveedor=p_id_prov, sku=p_sku, nombre=p_nombre, descripcion=p_desc, precio=p_precio, estado=p_estado WHERE id_producto=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM PRODUCTO WHERE id_producto = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_producto, sku, nombre, descripcion, precio, estado FROM PRODUCTO ORDER BY id_producto;
    END;

    FUNCTION contar_activos RETURN NUMBER IS
        v_n NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_n FROM PRODUCTO WHERE estado = 'ACTIVO';
        RETURN v_n;
    END;
END pkg_producto;
/

-- Paquete 4: Gestion de Usuarios
CREATE OR REPLACE PACKAGE pkg_usuario AS
    PROCEDURE insertar(p_id_rol NUMBER, p_nombre VARCHAR2, p_email VARCHAR2, p_pass VARCHAR2, p_estado VARCHAR2);
    PROCEDURE actualizar(p_id NUMBER, p_id_rol NUMBER, p_nombre VARCHAR2, p_email VARCHAR2, p_pass VARCHAR2, p_estado VARCHAR2);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  existe_email(p_email VARCHAR2) RETURN VARCHAR2;
END pkg_usuario;
/

CREATE OR REPLACE PACKAGE BODY pkg_usuario AS
    PROCEDURE insertar(p_id_rol NUMBER, p_nombre VARCHAR2, p_email VARCHAR2, p_pass VARCHAR2, p_estado VARCHAR2) IS
    BEGIN
        INSERT INTO USUARIO (id_rol, nombre, email, password_hash, estado) VALUES (p_id_rol, p_nombre, p_email, p_pass, p_estado);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_id_rol NUMBER, p_nombre VARCHAR2, p_email VARCHAR2, p_pass VARCHAR2, p_estado VARCHAR2) IS
    BEGIN
        UPDATE USUARIO SET id_rol=p_id_rol, nombre=p_nombre, email=p_email, password_hash=p_pass, estado=p_estado WHERE id_usuario=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM USUARIO WHERE id_usuario = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_usuario, nombre, email, estado FROM USUARIO ORDER BY id_usuario;
    END;

    FUNCTION existe_email(p_email VARCHAR2) RETURN VARCHAR2 IS
        v_n NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_n FROM USUARIO WHERE email = p_email;
        IF v_n > 0 THEN RETURN 'SI'; ELSE RETURN 'NO'; END IF;
    END;
END pkg_usuario;
/

-- Paquete 5: Gestion de Inventario
CREATE OR REPLACE PACKAGE pkg_inventario AS
    PROCEDURE insertar(p_id_producto NUMBER, p_stock_actual NUMBER, p_stock_min NUMBER, p_stock_max NUMBER);
    PROCEDURE actualizar(p_id NUMBER, p_stock_actual NUMBER, p_stock_min NUMBER, p_stock_max NUMBER);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  stock_actual(p_id_producto NUMBER) RETURN NUMBER;
END pkg_inventario;
/

CREATE OR REPLACE PACKAGE BODY pkg_inventario AS
    PROCEDURE insertar(p_id_producto NUMBER, p_stock_actual NUMBER, p_stock_min NUMBER, p_stock_max NUMBER) IS
    BEGIN
        INSERT INTO INVENTARIO (id_producto, stock_actual, stock_minimo, stock_maximo) VALUES (p_id_producto, p_stock_actual, p_stock_min, p_stock_max);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_stock_actual NUMBER, p_stock_min NUMBER, p_stock_max NUMBER) IS
    BEGIN
        UPDATE INVENTARIO SET stock_actual=p_stock_actual, stock_minimo=p_stock_min, stock_maximo=p_stock_max WHERE id_inventario=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM INVENTARIO WHERE id_inventario = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_inventario, id_producto, stock_actual, stock_minimo, stock_maximo FROM INVENTARIO ORDER BY id_inventario;
    END;

    FUNCTION stock_actual(p_id_producto NUMBER) RETURN NUMBER IS
        v_s NUMBER;
    BEGIN
        SELECT stock_actual INTO v_s FROM INVENTARIO WHERE id_producto = p_id_producto;
        RETURN v_s;
    EXCEPTION WHEN NO_DATA_FOUND THEN RETURN 0;
    END;
END pkg_inventario;
/

-- Paquete 6: Gestion de Movimientos
CREATE OR REPLACE PACKAGE pkg_movimiento AS
    PROCEDURE insertar(p_id_usuario NUMBER, p_tipo VARCHAR2, p_obs VARCHAR2);
    PROCEDURE actualizar(p_id NUMBER, p_id_usuario NUMBER, p_tipo VARCHAR2, p_obs VARCHAR2);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  total_por_usuario(p_id_usuario NUMBER) RETURN NUMBER;
END pkg_movimiento;
/

CREATE OR REPLACE PACKAGE BODY pkg_movimiento AS
    PROCEDURE insertar(p_id_usuario NUMBER, p_tipo VARCHAR2, p_obs VARCHAR2) IS
    BEGIN
        INSERT INTO MOVIMIENTO (id_usuario, tipo, observacion) VALUES (p_id_usuario, p_tipo, p_obs);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_id_usuario NUMBER, p_tipo VARCHAR2, p_obs VARCHAR2) IS
    BEGIN
        UPDATE MOVIMIENTO SET id_usuario=p_id_usuario, tipo=p_tipo, observacion=p_obs WHERE id_movimiento=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM MOVIMIENTO WHERE id_movimiento = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_movimiento, id_usuario, tipo, fecha, observacion FROM MOVIMIENTO ORDER BY id_movimiento;
    END;

    FUNCTION total_por_usuario(p_id_usuario NUMBER) RETURN NUMBER IS
        v_n NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_n FROM MOVIMIENTO WHERE id_usuario = p_id_usuario;
        RETURN v_n;
    END;
END pkg_movimiento;
/

-- Paquete 7: Reportes de Inventario
CREATE OR REPLACE PACKAGE pkg_reporte_inventario AS
    PROCEDURE productos_stock_bajo(p_resultado OUT SYS_REFCURSOR);
    PROCEDURE valor_por_categoria(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  valor_total RETURN NUMBER;
    FUNCTION  productos_bajo_count RETURN NUMBER;
END pkg_reporte_inventario;
/

CREATE OR REPLACE PACKAGE BODY pkg_reporte_inventario AS
    PROCEDURE productos_stock_bajo(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR
            SELECT p.sku, p.nombre, i.stock_actual, i.stock_minimo
            FROM INVENTARIO i JOIN PRODUCTO p ON p.id_producto = i.id_producto
            WHERE i.stock_actual <= i.stock_minimo;
    END;

    PROCEDURE valor_por_categoria(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR
            SELECT c.nombre AS categoria,
                   SUM(i.stock_actual * p.precio) AS valor_total
            FROM CATEGORIA c
            JOIN PRODUCTO p   ON p.id_categoria = c.id_categoria
            JOIN INVENTARIO i ON i.id_producto  = p.id_producto
            GROUP BY c.nombre;
    END;

    FUNCTION valor_total RETURN NUMBER IS
        v_t NUMBER;
    BEGIN
        SELECT SUM(i.stock_actual * p.precio) INTO v_t
        FROM INVENTARIO i JOIN PRODUCTO p ON p.id_producto = i.id_producto WHERE p.estado='ACTIVO';
        RETURN NVL(v_t,0);
    END;

    FUNCTION productos_bajo_count RETURN NUMBER IS
        v_n NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_n FROM INVENTARIO WHERE stock_actual <= stock_minimo;
        RETURN v_n;
    END;
END pkg_reporte_inventario;
/

-- Paquete 8: Utilidades generales
CREATE OR REPLACE PACKAGE pkg_util AS
    FUNCTION  formato_precio(p_precio NUMBER) RETURN VARCHAR2;
    FUNCTION  estado_activo(p_estado VARCHAR2) RETURN VARCHAR2;
    PROCEDURE contar_registros(p_tabla IN VARCHAR2, p_resultado OUT NUMBER);
END pkg_util;
/

CREATE OR REPLACE PACKAGE BODY pkg_util AS
    FUNCTION formato_precio(p_precio NUMBER) RETURN VARCHAR2 IS
    BEGIN
        RETURN '₡' || TO_CHAR(p_precio, 'FM999,999,990.00');
    END;

    FUNCTION estado_activo(p_estado VARCHAR2) RETURN VARCHAR2 IS
    BEGIN
        IF p_estado = 'ACTIVO' THEN RETURN 'Sí'; ELSE RETURN 'No'; END IF;
    END;

    PROCEDURE contar_registros(p_tabla IN VARCHAR2, p_resultado OUT NUMBER) IS
    BEGIN
        EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM ' || p_tabla INTO p_resultado;
    EXCEPTION WHEN OTHERS THEN p_resultado := -1;
    END;
END pkg_util;
/

-- Paquete 9: Gestion de Roles
CREATE OR REPLACE PACKAGE pkg_rol AS
    PROCEDURE insertar(p_nombre VARCHAR2);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  nombre_rol(p_id NUMBER) RETURN VARCHAR2;
END pkg_rol;
/

CREATE OR REPLACE PACKAGE BODY pkg_rol AS
    PROCEDURE insertar(p_nombre VARCHAR2) IS
    BEGIN
        INSERT INTO ROL (nombre) VALUES (p_nombre);
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM ROL WHERE id_rol = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_rol, nombre FROM ROL ORDER BY id_rol;
    END;

    FUNCTION nombre_rol(p_id NUMBER) RETURN VARCHAR2 IS
        v_n VARCHAR2(50);
    BEGIN
        SELECT nombre INTO v_n FROM ROL WHERE id_rol = p_id;
        RETURN v_n;
    EXCEPTION WHEN NO_DATA_FOUND THEN RETURN 'NO_ENCONTRADO';
    END;
END pkg_rol;
/

-- Paquete 10: Gestion de Detalle de Movimientos
CREATE OR REPLACE PACKAGE pkg_movimiento_detalle AS
    PROCEDURE insertar(p_id_mov NUMBER, p_id_prod NUMBER, p_cantidad NUMBER, p_costo NUMBER);
    PROCEDURE actualizar(p_id NUMBER, p_id_mov NUMBER, p_id_prod NUMBER, p_cantidad NUMBER, p_costo NUMBER);
    PROCEDURE eliminar(p_id NUMBER);
    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR);
    FUNCTION  subtotal_movimiento(p_id_movimiento NUMBER) RETURN NUMBER;
END pkg_movimiento_detalle;
/

CREATE OR REPLACE PACKAGE BODY pkg_movimiento_detalle AS
    PROCEDURE insertar(p_id_mov NUMBER, p_id_prod NUMBER, p_cantidad NUMBER, p_costo NUMBER) IS
    BEGIN
        INSERT INTO MOVIMIENTO_DETALLE (id_movimiento, id_producto, cantidad, costo_unitario) VALUES (p_id_mov, p_id_prod, p_cantidad, p_costo);
    END;

    PROCEDURE actualizar(p_id NUMBER, p_id_mov NUMBER, p_id_prod NUMBER, p_cantidad NUMBER, p_costo NUMBER) IS
    BEGIN
        UPDATE MOVIMIENTO_DETALLE SET id_movimiento=p_id_mov, id_producto=p_id_prod, cantidad=p_cantidad, costo_unitario=p_costo WHERE id_detalle=p_id;
    END;

    PROCEDURE eliminar(p_id NUMBER) IS
    BEGIN
        DELETE FROM MOVIMIENTO_DETALLE WHERE id_detalle = p_id;
    END;

    PROCEDURE listar(p_resultado OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_resultado FOR SELECT id_detalle, id_movimiento, id_producto, cantidad, costo_unitario FROM MOVIMIENTO_DETALLE ORDER BY id_detalle;
    END;

    FUNCTION subtotal_movimiento(p_id_movimiento NUMBER) RETURN NUMBER IS
        v_t NUMBER;
    BEGIN
        SELECT SUM(cantidad * costo_unitario) INTO v_t FROM MOVIMIENTO_DETALLE WHERE id_movimiento = p_id_movimiento;
        RETURN NVL(v_t, 0);
    END;
END pkg_movimiento_detalle;
/


-- =====================================================
-- TRIGGERS (5 triggers)
-- =====================================================

-- Trigger 1: Actualizar stock al insertar un detalle de movimiento
CREATE OR REPLACE TRIGGER trg_actualizar_stock_insert
AFTER INSERT ON MOVIMIENTO_DETALLE
FOR EACH ROW
DECLARE
    v_tipo MOVIMIENTO.TIPO%TYPE;
BEGIN
    SELECT tipo INTO v_tipo FROM MOVIMIENTO WHERE id_movimiento = :NEW.id_movimiento;

    IF v_tipo = 'ENTRADA' THEN
        UPDATE INVENTARIO
           SET stock_actual = stock_actual + :NEW.cantidad
         WHERE id_producto = :NEW.id_producto;
    ELSIF v_tipo = 'SALIDA' THEN
        UPDATE INVENTARIO
           SET stock_actual = stock_actual - :NEW.cantidad
         WHERE id_producto = :NEW.id_producto;
    END IF;
END;
/

-- Trigger 2: Evitar stock negativo en salidas
CREATE OR REPLACE TRIGGER trg_validar_stock_salida
BEFORE INSERT ON MOVIMIENTO_DETALLE
FOR EACH ROW
DECLARE
    v_tipo       MOVIMIENTO.TIPO%TYPE;
    v_stock_act  NUMBER;
BEGIN
    SELECT tipo INTO v_tipo FROM MOVIMIENTO WHERE id_movimiento = :NEW.id_movimiento;

    IF v_tipo = 'SALIDA' THEN
        SELECT stock_actual INTO v_stock_act FROM INVENTARIO WHERE id_producto = :NEW.id_producto;
        IF v_stock_act < :NEW.cantidad THEN
            RAISE_APPLICATION_ERROR(-20001, 'Stock insuficiente para el producto ID: ' || :NEW.id_producto);
        END IF;
    END IF;
END;
/

-- Trigger 3: Registrar fecha de creacion del usuario automaticamente
CREATE OR REPLACE TRIGGER trg_fecha_creacion_usuario
BEFORE INSERT ON USUARIO
FOR EACH ROW
BEGIN
    IF :NEW.fecha_creacion IS NULL THEN
        :NEW.fecha_creacion := SYSDATE;
    END IF;
END;
/

-- Trigger 4: Evitar eliminar una categoria que tiene productos activos
CREATE OR REPLACE TRIGGER trg_proteger_categoria
BEFORE DELETE ON CATEGORIA
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM PRODUCTO
    WHERE id_categoria = :OLD.id_categoria AND estado = 'ACTIVO';

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'No se puede eliminar la categoría porque tiene productos activos.');
    END IF;
END;
/

-- Trigger 5: Evitar eliminar un proveedor que tiene productos activos
CREATE OR REPLACE TRIGGER trg_proteger_proveedor
BEFORE DELETE ON PROVEEDOR
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM PRODUCTO
    WHERE id_proveedor = :OLD.id_proveedor AND estado = 'ACTIVO';

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'No se puede eliminar el proveedor porque tiene productos activos asociados.');
    END IF;
END;
/


-- =====================================================
-- CURSORES EXPLICITOS (15 cursores en procedimientos dedicados)
-- =====================================================

-- Cursor 1: Listar productos con stock bajo usando cursor explicito
CREATE OR REPLACE PROCEDURE cur_productos_stock_bajo
AS
    CURSOR c_bajo IS
        SELECT p.sku, p.nombre, i.stock_actual, i.stock_minimo
        FROM INVENTARIO i
        JOIN PRODUCTO p ON p.id_producto = i.id_producto
        WHERE i.stock_actual <= i.stock_minimo;
    v_reg c_bajo%ROWTYPE;
BEGIN
    OPEN c_bajo;
    LOOP
        FETCH c_bajo INTO v_reg;
        EXIT WHEN c_bajo%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SKU: ' || v_reg.sku || ' | Producto: ' || v_reg.nombre ||
                             ' | Stock: ' || v_reg.stock_actual || ' | Minimo: ' || v_reg.stock_minimo);
    END LOOP;
    CLOSE c_bajo;
END;
/

-- Cursor 2: Listar todos los productos activos
CREATE OR REPLACE PROCEDURE cur_listar_productos_activos
AS
    CURSOR c_prod IS
        SELECT sku, nombre, precio FROM PRODUCTO WHERE estado = 'ACTIVO' ORDER BY nombre;
    v_reg c_prod%ROWTYPE;
BEGIN
    OPEN c_prod;
    LOOP
        FETCH c_prod INTO v_reg;
        EXIT WHEN c_prod%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SKU: ' || v_reg.sku || ' | ' || v_reg.nombre || ' | ₡' || v_reg.precio);
    END LOOP;
    CLOSE c_prod;
END;
/

-- Cursor 3: Listar categorias con conteo de productos
CREATE OR REPLACE PROCEDURE cur_categorias_con_productos
AS
    CURSOR c_cat IS
        SELECT c.nombre AS categoria, COUNT(p.id_producto) AS total
        FROM CATEGORIA c
        LEFT JOIN PRODUCTO p ON p.id_categoria = c.id_categoria
        GROUP BY c.nombre;
    v_cat  VARCHAR2(80);
    v_total NUMBER;
BEGIN
    OPEN c_cat;
    LOOP
        FETCH c_cat INTO v_cat, v_total;
        EXIT WHEN c_cat%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Categoria: ' || v_cat || ' | Productos: ' || v_total);
    END LOOP;
    CLOSE c_cat;
END;
/

-- Cursor 4: Listar usuarios activos con su rol
CREATE OR REPLACE PROCEDURE cur_usuarios_activos
AS
    CURSOR c_usr IS
        SELECT u.nombre, u.email, r.nombre AS rol
        FROM USUARIO u
        JOIN ROL r ON r.id_rol = u.id_rol
        WHERE u.estado = 'ACTIVO';
    v_nombre VARCHAR2(120);
    v_email  VARCHAR2(120);
    v_rol    VARCHAR2(50);
BEGIN
    OPEN c_usr;
    LOOP
        FETCH c_usr INTO v_nombre, v_email, v_rol;
        EXIT WHEN c_usr%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Usuario: ' || v_nombre || ' | Email: ' || v_email || ' | Rol: ' || v_rol);
    END LOOP;
    CLOSE c_usr;
END;
/

-- Cursor 5: Listar proveedores activos
CREATE OR REPLACE PROCEDURE cur_proveedores_activos
AS
    CURSOR c_prov IS
        SELECT nombre, email, telefono FROM PROVEEDOR WHERE estado = 'ACTIVO';
    v_reg c_prov%ROWTYPE;
BEGIN
    OPEN c_prov;
    LOOP
        FETCH c_prov INTO v_reg;
        EXIT WHEN c_prov%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Proveedor: ' || v_reg.nombre || ' | Email: ' || v_reg.email || ' | Tel: ' || v_reg.telefono);
    END LOOP;
    CLOSE c_prov;
END;
/

-- Cursor 6: Listar movimientos del mes actual
CREATE OR REPLACE PROCEDURE cur_movimientos_mes_actual
AS
    CURSOR c_mov IS
        SELECT m.id_movimiento, u.nombre AS usuario, m.tipo, m.fecha
        FROM MOVIMIENTO m
        JOIN USUARIO u ON u.id_usuario = m.id_usuario
        WHERE TRUNC(m.fecha, 'MM') = TRUNC(SYSDATE, 'MM');
    v_id    NUMBER;
    v_usr   VARCHAR2(120);
    v_tipo  VARCHAR2(20);
    v_fecha DATE;
BEGIN
    OPEN c_mov;
    LOOP
        FETCH c_mov INTO v_id, v_usr, v_tipo, v_fecha;
        EXIT WHEN c_mov%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_id || ' | Usuario: ' || v_usr || ' | Tipo: ' || v_tipo || ' | Fecha: ' || TO_CHAR(v_fecha,'DD/MM/YYYY'));
    END LOOP;
    CLOSE c_mov;
END;
/

-- Cursor 7: Calcular valor total del inventario por producto
CREATE OR REPLACE PROCEDURE cur_valor_inventario
AS
    CURSOR c_val IS
        SELECT p.nombre, i.stock_actual, p.precio,
               (i.stock_actual * p.precio) AS valor
        FROM INVENTARIO i
        JOIN PRODUCTO p ON p.id_producto = i.id_producto
        ORDER BY valor DESC;
    v_nombre  VARCHAR2(120);
    v_stock   NUMBER;
    v_precio  NUMBER;
    v_valor   NUMBER;
    v_total   NUMBER := 0;
BEGIN
    OPEN c_val;
    LOOP
        FETCH c_val INTO v_nombre, v_stock, v_precio, v_valor;
        EXIT WHEN c_val%NOTFOUND;
        v_total := v_total + v_valor;
        DBMS_OUTPUT.PUT_LINE(v_nombre || ' | Stock: ' || v_stock || ' | ₡' || v_precio || ' | Total: ₡' || v_valor);
    END LOOP;
    CLOSE c_val;
    DBMS_OUTPUT.PUT_LINE('=== VALOR TOTAL INVENTARIO: ₡' || v_total || ' ===');
END;
/

-- Cursor 8: Listar detalle de un movimiento especifico
CREATE OR REPLACE PROCEDURE cur_detalle_movimiento (
    p_id_movimiento IN NUMBER
)
AS
    CURSOR c_det IS
        SELECT p.sku, p.nombre, md.cantidad, md.costo_unitario,
               (md.cantidad * md.costo_unitario) AS subtotal
        FROM MOVIMIENTO_DETALLE md
        JOIN PRODUCTO p ON p.id_producto = md.id_producto
        WHERE md.id_movimiento = p_id_movimiento;
    v_sku      VARCHAR2(50);
    v_nombre   VARCHAR2(120);
    v_cant     NUMBER;
    v_costo    NUMBER;
    v_sub      NUMBER;
BEGIN
    OPEN c_det;
    LOOP
        FETCH c_det INTO v_sku, v_nombre, v_cant, v_costo, v_sub;
        EXIT WHEN c_det%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SKU: ' || v_sku || ' | ' || v_nombre || ' | Cant: ' || v_cant || ' | Costo: ₡' || v_costo || ' | Sub: ₡' || v_sub);
    END LOOP;
    CLOSE c_det;
END;
/

-- Cursor 9: Productos sin inventario registrado
CREATE OR REPLACE PROCEDURE cur_productos_sin_inventario
AS
    CURSOR c_sin IS
        SELECT p.id_producto, p.sku, p.nombre
        FROM PRODUCTO p
        WHERE NOT EXISTS (SELECT 1 FROM INVENTARIO i WHERE i.id_producto = p.id_producto);
    v_reg c_sin%ROWTYPE;
BEGIN
    OPEN c_sin;
    LOOP
        FETCH c_sin INTO v_reg;
        EXIT WHEN c_sin%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SIN INVENTARIO -> ID: ' || v_reg.id_producto || ' | SKU: ' || v_reg.sku || ' | ' || v_reg.nombre);
    END LOOP;
    CLOSE c_sin;
END;
/

-- Cursor 10: Resumen de entradas y salidas por producto
CREATE OR REPLACE PROCEDURE cur_resumen_entradas_salidas
AS
    CURSOR c_res IS
        SELECT p.sku,
               p.nombre,
               SUM(CASE WHEN m.tipo = 'ENTRADA' THEN md.cantidad ELSE 0 END) AS total_entradas,
               SUM(CASE WHEN m.tipo = 'SALIDA'  THEN md.cantidad ELSE 0 END) AS total_salidas
        FROM MOVIMIENTO_DETALLE md
        JOIN MOVIMIENTO m ON m.id_movimiento = md.id_movimiento
        JOIN PRODUCTO   p ON p.id_producto   = md.id_producto
        GROUP BY p.sku, p.nombre;
    v_sku      VARCHAR2(50);
    v_nombre   VARCHAR2(120);
    v_ent      NUMBER;
    v_sal      NUMBER;
BEGIN
    OPEN c_res;
    LOOP
        FETCH c_res INTO v_sku, v_nombre, v_ent, v_sal;
        EXIT WHEN c_res%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SKU: ' || v_sku || ' | ' || v_nombre || ' | Entradas: ' || v_ent || ' | Salidas: ' || v_sal);
    END LOOP;
    CLOSE c_res;
END;
/

-- Cursor 11: Listar productos por proveedor
CREATE OR REPLACE PROCEDURE cur_productos_por_proveedor (
    p_id_proveedor IN NUMBER
)
AS
    CURSOR c_pp IS
        SELECT p.sku, p.nombre, p.precio, p.estado
        FROM PRODUCTO p
        WHERE p.id_proveedor = p_id_proveedor;
    v_reg c_pp%ROWTYPE;
BEGIN
    OPEN c_pp;
    LOOP
        FETCH c_pp INTO v_reg;
        EXIT WHEN c_pp%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SKU: ' || v_reg.sku || ' | ' || v_reg.nombre || ' | ₡' || v_reg.precio || ' | ' || v_reg.estado);
    END LOOP;
    CLOSE c_pp;
END;
/

-- Cursor 12: Usuarios sin movimientos registrados
CREATE OR REPLACE PROCEDURE cur_usuarios_sin_movimientos
AS
    CURSOR c_usm IS
        SELECT u.id_usuario, u.nombre, u.email
        FROM USUARIO u
        WHERE NOT EXISTS (SELECT 1 FROM MOVIMIENTO m WHERE m.id_usuario = u.id_usuario);
    v_reg c_usm%ROWTYPE;
BEGIN
    OPEN c_usm;
    LOOP
        FETCH c_usm INTO v_reg;
        EXIT WHEN c_usm%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('SIN MOVIMIENTOS -> ID: ' || v_reg.id_usuario || ' | ' || v_reg.nombre || ' | ' || v_reg.email);
    END LOOP;
    CLOSE c_usm;
END;
/

-- Cursor 13: Productos con stock maximo alcanzado
CREATE OR REPLACE PROCEDURE cur_stock_maximo_alcanzado
AS
    CURSOR c_max IS
        SELECT p.sku, p.nombre, i.stock_actual, i.stock_maximo
        FROM INVENTARIO i
        JOIN PRODUCTO p ON p.id_producto = i.id_producto
        WHERE i.stock_maximo IS NOT NULL AND i.stock_actual >= i.stock_maximo;
    v_reg c_max%ROWTYPE;
BEGIN
    OPEN c_max;
    LOOP
        FETCH c_max INTO v_reg;
        EXIT WHEN c_max%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('STOCK MAX -> SKU: ' || v_reg.sku || ' | ' || v_reg.nombre || ' | Stock: ' || v_reg.stock_actual || ' | Max: ' || v_reg.stock_maximo);
    END LOOP;
    CLOSE c_max;
END;
/

-- Cursor 14: Listar movimientos de tipo AJUSTE
CREATE OR REPLACE PROCEDURE cur_movimientos_ajuste
AS
    CURSOR c_aj IS
        SELECT m.id_movimiento, u.nombre AS usuario, m.fecha, m.observacion
        FROM MOVIMIENTO m
        JOIN USUARIO u ON u.id_usuario = m.id_usuario
        WHERE m.tipo = 'AJUSTE';
    v_id    NUMBER;
    v_usr   VARCHAR2(120);
    v_fecha DATE;
    v_obs   VARCHAR2(255);
BEGIN
    OPEN c_aj;
    LOOP
        FETCH c_aj INTO v_id, v_usr, v_fecha, v_obs;
        EXIT WHEN c_aj%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_id || ' | Usuario: ' || v_usr || ' | Fecha: ' || TO_CHAR(v_fecha,'DD/MM/YYYY') || ' | Obs: ' || NVL(v_obs,'--'));
    END LOOP;
    CLOSE c_aj;
END;
/

-- Cursor 15: Calcular el top 3 productos con mayor valor en inventario
CREATE OR REPLACE PROCEDURE cur_top3_valor_inventario
AS
    CURSOR c_top IS
        SELECT p.nombre, (i.stock_actual * p.precio) AS valor
        FROM INVENTARIO i
        JOIN PRODUCTO p ON p.id_producto = i.id_producto
        ORDER BY valor DESC
        FETCH FIRST 3 ROWS ONLY;
    v_nombre VARCHAR2(120);
    v_valor  NUMBER;
    v_pos    NUMBER := 1;
BEGIN
    OPEN c_top;
    LOOP
        FETCH c_top INTO v_nombre, v_valor;
        EXIT WHEN c_top%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('#' || v_pos || ' ' || v_nombre || ' - Valor: ₡' || v_valor);
        v_pos := v_pos + 1;
    END LOOP;
    CLOSE c_top;
END;
/
