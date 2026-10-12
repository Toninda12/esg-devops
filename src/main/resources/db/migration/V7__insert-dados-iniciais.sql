INSERT INTO TB_USUARIO
VALUES (
    1,
    'Administrador',
    'admin@esg.com',
    '123456',
    '19999999999',
    'ADMINISTRADOR',
    CURRENT_TIMESTAMP
);

INSERT INTO TB_RESIDUO
VALUES (
    1,
    'Plástico',
    'Garrafas PET',
    'Lavar antes do descarte',
    'Reciclagem',
    'PLASTICO',
    0,
    CURRENT_TIMESTAMP
);

INSERT INTO TB_PONTO_COLETA
VALUES (
    1,
    'Ecoponto Centro',
    'Rua Central 100',
    NULL,
    NULL,
    'PLASTICO',
    1000,
    250,
    80,
    'ATIVO',
    CURRENT_TIMESTAMP
);