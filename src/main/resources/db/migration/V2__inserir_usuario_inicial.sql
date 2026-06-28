INSERT INTO usuarios (login, senha, nome, perfil)
VALUES (
    'victorhugo',
    encode(sha256('20252021847'::bytea), 'hex'),
    'Victor Hugo Rodrigues Silverio',
    'ADMIN'
)
ON CONFLICT (login) DO NOTHING;
