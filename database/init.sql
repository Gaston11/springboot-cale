-- Usuario demo
INSERT INTO usuario
    (id, fecha_creacion, fecha_modificacion, apellido, email, nombre, password, prioridad, rol)
VALUES
    (1, NOW(), NOW(), 'Demo', 'demo@cale.com', 'Usuario',
     '$2a$10$n/ly6vNuDMlNtFEehTPHleX9.cpZ6uReAWtBfInH22UDqd/SzDqWm',
     1, 'USER');

-- Categoría demo
INSERT INTO categoria
    (id, fecha_creacion, fecha_modificacion, nombre)
VALUES
    (1, NOW(), NOW(), 'Tecnología');

-- Post demo
INSERT INTO post
    (id, fecha_creacion, fecha_modificacion, descripcion, titulo, usuario_id)
VALUES
    (1, NOW(), NOW(), 'Este es un post de demostración para probar la API.',
     'Mi primer post', 1);

-- Relación post-categoría
INSERT INTO post_categoria
    (post_id, categoria_id)
VALUES
    (1, 1);

-- Comentario demo
INSERT INTO comentario_model
    (id, fecha_creacion, fecha_modificacion, comentario, post_id, usuario_id)
VALUES
    (1, NOW(), NOW(), 'Este es un comentario de demostración.', 1, 1);