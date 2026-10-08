DROP SCHEMA IF EXISTS eocape CASCADE;
CREATE SCHEMA eocape;
SET search_path TO eocape, public;
SET TIME ZONE 'America/Sao_Paulo';

-- INFORMAÇÕES DO CONDOMÍNIO SENDO USADO PELO  
CREATE TABLE condominios (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome          text NOT NULL,
    cnpj          char(14) UNIQUE,
    logradouro    text NOT NULL,
    numero        text NOT NULL,
    complemento   text,
    bairro        text NOT NULL,
    cidade        text NOT NULL,
    uf            char(2) NOT NULL,
    cep           char(8) NOT NULL,
    telefone      text,
    email_contato text,
    ativo         boolean NOT NULL DEFAULT true,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);

-- BLOCOS DO CONDOMINIO
CREATE TABLE blocos (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominios(id),
    nome          text NOT NULL,
    ativo         boolean NOT NULL DEFAULT true,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now(),
    UNIQUE (condominio_id, nome)
);

-- UNIDADES DO CONDOMINIO (APARTAMENTOS)
CREATE TABLE unidades (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bloco_id      uuid NOT NULL REFERENCES blocos(id),
    numero        text NOT NULL,
    andar         integer,
    ativo         boolean NOT NULL DEFAULT true,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now(),
    UNIQUE (bloco_id, numero)
);

-- AREAS DO CONDOMINIO (EM GERAL)
CREATE TABLE areas (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominios(id),
    nome          text NOT NULL,
    descricao     text,
    localizacao   text,
    ativo         boolean NOT NULL DEFAULT true,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now(),
    UNIQUE (condominio_id, nome)
);

-- AREAS QUE PODEM SER RESERVADAS NO CONDOMINIO
CREATE TABLE espacos_comuns (
    id                       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    area_id                  uuid NOT NULL UNIQUE REFERENCES areas(id),
    capacidade               integer,
    horario_abertura         time,
    horario_fechamento       time,
    antecedencia_minima_dias integer NOT NULL DEFAULT 0,
    antecedencia_maxima_dias integer,
    duracao_maxima_horas     numeric(4,1),
    taxa                     numeric(10,2) NOT NULL DEFAULT 0,
    exige_aprovacao          boolean NOT NULL DEFAULT false,
    regras                   text,
    ativo                    boolean NOT NULL DEFAULT true,
    criado_em                timestamptz NOT NULL DEFAULT now(),
    atualizado_em            timestamptz NOT NULL DEFAULT now()
);

-- USUARIOS, PODEM SER TANTO FUNCIONARIOS QUANTO MORADORES
CREATE TABLE usuarios (
    id                        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf                       char(11) NOT NULL UNIQUE,
    nome                      text NOT NULL,
    email                     text UNIQUE,
    telefone                  text,
    senha_hash                text NOT NULL,
    primeiro_acesso_concluido boolean NOT NULL DEFAULT false,
    ativo                     boolean NOT NULL DEFAULT true,
    ultimo_login_em           timestamptz,
    criado_em                 timestamptz NOT NULL DEFAULT now(),
    atualizado_em             timestamptz NOT NULL DEFAULT now()
);

-- LIGA OS USUARIOS COM SEUS APARTAMENTOS
-- SE A data_fim FOR VAZIA O APARTAMENTO AINDA ESTÁ ATIVO
CREATE TABLE vinculos_unidades (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id    uuid NOT NULL REFERENCES usuarios(id),
    unidade_id    uuid NOT NULL REFERENCES unidades(id),
    tipo_vinculo  text NOT NULL CHECK (tipo_vinculo IN ('proprietario', 'inquilino', 'dependente')),
    principal     boolean NOT NULL DEFAULT false,
    data_inicio   date NOT NULL DEFAULT current_date,
    data_fim      date,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, unidade_id, data_inicio)
);

-- PAPEL DE CADA FUNCIONARIO NO CONDOMINIO
CREATE TABLE papeis_condominios (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id    uuid NOT NULL REFERENCES usuarios(id),
    condominio_id uuid NOT NULL REFERENCES condominios(id),
    papel         text NOT NULL CHECK (papel IN ('morador', 'sindico', 'subsindico', 'porteiro', 'zelador')),
    data_inicio   date NOT NULL DEFAULT current_date,
    data_fim      date,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, condominio_id, papel, data_inicio)
);

-- CREATE TABLE contatos_emergencia (
--     id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
--     usuario_id           uuid NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
--     nome                 text NOT NULL,
--     parentesco           text,
--     telefone             text NOT NULL,
--     telefone_alternativo text,
--     ordem                integer NOT NULL DEFAULT 1,   -- quem chamar primeiro
--     observacao           text,
--     criado_em            timestamptz NOT NULL DEFAULT now(),
--     atualizado_em        timestamptz NOT NULL DEFAULT now()
-- );

-- CODIGOS PARA PRIMEIRO ACESSO - TEMP
CREATE TABLE verificacoes_emails (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id  uuid NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    email       text NOT NULL,
    codigo_hash text NOT NULL,
    expira_em   timestamptz NOT NULL,
    usado_em    timestamptz,
    tentativas  integer NOT NULL DEFAULT 0,
    criado_em   timestamptz NOT NULL DEFAULT now()
);

-- AVISOS DO SINDICO
CREATE TABLE avisos (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominios(id),
    autor_id      uuid NOT NULL REFERENCES usuarios(id),
    bloco_id      uuid REFERENCES blocos(id), -- VAZIO = AVISAR PARA TODOS 
    titulo        text NOT NULL,
    conteudo      text NOT NULL,
    importante    boolean NOT NULL DEFAULT false,
    fixado        boolean NOT NULL DEFAULT false,
    publicado_em  timestamptz NOT NULL DEFAULT now(),
    expira_em     timestamptz,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);

-- ATAS DO CONDOMINIO
CREATE TABLE atas (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominios(id),
    criado_por    uuid NOT NULL REFERENCES usuarios(id),
    titulo        text NOT NULL,
    tipo          text NOT NULL CHECK (tipo IN ('ordinaria', 'extraordinaria')),
    data_reuniao  date NOT NULL,
    resumo        text,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);

-- OCORRENCIAS DOS USUARIOS
CREATE TABLE chamados (
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id  uuid NOT NULL REFERENCES condominios(id),
    usuario_id     uuid NOT NULL REFERENCES usuarios(id),
    unidade_id     uuid REFERENCES unidades(id),
    area_id        uuid REFERENCES areas(id),
    responsavel_id uuid REFERENCES usuarios(id),
    titulo         text NOT NULL,
    descricao      text NOT NULL,
    categoria      text NOT NULL DEFAULT 'outros',
    status         text NOT NULL DEFAULT 'aberto'
                   CHECK (status IN ('aberto', 'em_analise', 'em_andamento', 'resolvido', 'cancelado')),
    prioridade     text NOT NULL DEFAULT 'media'
                   CHECK (prioridade IN ('baixa', 'media', 'alta', 'urgente')),
    concluido_em   timestamptz,
    criado_em      timestamptz NOT NULL DEFAULT now(),
    atualizado_em  timestamptz NOT NULL DEFAULT now()
);

-- ARMAZENA A TROCA DE STATUS DO CHAMADO
CREATE TABLE historicos_chamados (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    chamado_id      uuid NOT NULL REFERENCES chamados(id) ON DELETE CASCADE,
    usuario_id      uuid NOT NULL REFERENCES usuarios(id),  -- quem fez a mudanca
    status_anterior text,
    status_novo     text NOT NULL,
    comentario      text,
    visivel_morador boolean NOT NULL DEFAULT true, -- SE FOR FALSO, É SÓ UM DADO INTERNO
    criado_em       timestamptz NOT NULL DEFAULT now()
);

-- HISTORICO DE MANUTENCOES 
CREATE TABLE manutencoes (
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id  uuid NOT NULL REFERENCES condominios(id),
    unidade_id     uuid REFERENCES unidades(id),
    area_id        uuid REFERENCES areas(id),
    chamado_id     uuid REFERENCES chamados(id),            -- chamado que originou, se houver
    criado_por     uuid NOT NULL REFERENCES usuarios(id),
    titulo         text NOT NULL,
    descricao      text,
    tipo           text NOT NULL CHECK (tipo IN ('preventiva', 'corretiva')),
    status         text NOT NULL DEFAULT 'agendada'
                   CHECK (status IN ('agendada', 'em_andamento', 'concluida', 'cancelada')),
    prestador      text,
    custo          numeric(12,2),
    data_agendada  timestamptz,
    data_inicio    timestamptz,
    data_conclusao timestamptz,
    criado_em      timestamptz NOT NULL DEFAULT now(),
    atualizado_em  timestamptz NOT NULL DEFAULT now(),

    -- VERIFICA SE O CHAMADO É OU EM UMA APARTAMENTO OU EM UMA AREA
    CHECK ((unidade_id IS NOT NULL AND area_id IS NULL)
        OR (unidade_id IS NULL AND area_id IS NOT NULL))
);

-- AGENDAMENTO DOS ESPACOS
CREATE TABLE agendamentos_espacos (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id     uuid NOT NULL REFERENCES condominios(id),
    espaco_comum_id   uuid NOT NULL REFERENCES espacos_comuns(id),
    usuario_id        uuid NOT NULL REFERENCES usuarios(id),
    unidade_id        uuid NOT NULL REFERENCES unidades(id),  -- em nome de qual unidade
    inicio            timestamptz NOT NULL,
    fim               timestamptz NOT NULL,
    status            text NOT NULL DEFAULT 'pendente'
                      CHECK (status IN ('pendente', 'confirmado', 'cancelado', 'concluido')),
    numero_convidados integer,
    observacao        text,
    criado_em         timestamptz NOT NULL DEFAULT now(),
    atualizado_em     timestamptz NOT NULL DEFAULT now(),
    CHECK (fim > inicio)
);

-- "CLASSIFICADOS" DOS MORADORES
CREATE TABLE classificados (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominios(id),
    usuario_id    uuid NOT NULL REFERENCES usuarios(id),
    titulo        text NOT NULL,
    descricao     text NOT NULL,
    tipo          text NOT NULL CHECK (tipo IN ('venda', 'doacao', 'servico')),
    preco         numeric(12,2),
    status        text NOT NULL DEFAULT 'ativo' CHECK (status IN ('ativo', 'encerrado', 'expirado')),
    expira_em     timestamptz,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);



-- ARQUIVOS ENVIADOS À PLATAFORMA
CREATE TABLE arquivos (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id       uuid REFERENCES condominios(id),
    enviado_por         uuid NOT NULL REFERENCES usuarios(id),
    nome_original       text NOT NULL,
    chave_armazenamento text NOT NULL UNIQUE,
    tipo_mime           text NOT NULL,
    categoria           text NOT NULL CHECK (categoria IN ('foto', 'video', 'documento')),
    tamanho_bytes       bigint NOT NULL,
    criado_em           timestamptz NOT NULL DEFAULT now()
);

-- ARQUIVOS RELACIONADOS AOS CHAMADOS
CREATE TABLE arquivos_chamados (
    chamado_id           uuid NOT NULL REFERENCES chamados(id) ON DELETE CASCADE,
    arquivo_id           uuid NOT NULL REFERENCES arquivos(id) ON DELETE CASCADE,
    historico_chamado_id uuid REFERENCES historicos_chamados(id) ON DELETE SET NULL,
    criado_em            timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (chamado_id, arquivo_id)
);

-- ARQUIVOS RELACIONADOS ÀS MANUTENCOES
CREATE TABLE arquivos_manutencoes (
    manutencao_id uuid NOT NULL REFERENCES manutencoes(id) ON DELETE CASCADE,
    arquivo_id    uuid NOT NULL REFERENCES arquivos(id) ON DELETE CASCADE,
    momento       text CHECK (momento IN ('antes', 'depois', 'comprovante')),
    criado_em     timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (manutencao_id, arquivo_id)
);

-- ARQUIVOS RELACIONADOS AOS CLASSIFICADOS
CREATE TABLE arquivos_classificados (
    classificado_id uuid NOT NULL REFERENCES classificados(id) ON DELETE CASCADE,
    arquivo_id      uuid NOT NULL REFERENCES arquivos(id) ON DELETE CASCADE,
    ordem           integer NOT NULL DEFAULT 1,   -- qual foto aparece primeiro
    criado_em       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (classificado_id, arquivo_id)
);

-- ARQUIVOS RELACIONADOS ÀS ATAS
CREATE TABLE arquivos_atas (
    ata_id         uuid NOT NULL REFERENCES atas(id) ON DELETE CASCADE,
    arquivo_id     uuid NOT NULL REFERENCES arquivos(id) ON DELETE CASCADE,
    tipo_documento text NOT NULL DEFAULT 'ata' CHECK (tipo_documento IN ('ata', 'lista_presenca', 'anexo')),
    criado_em      timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (ata_id, arquivo_id)
);


-- POSSIVEIS USUARIOS
CREATE TABLE leads (
    id                    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                  text NOT NULL,
    email                 text NOT NULL,
    telefone              text,
    nome_condominio       text,
    perfil                text CHECK (perfil IN ('sindico', 'morador', 'administradora', 'outro')),
    origem                text,
    consentimento_contato boolean NOT NULL DEFAULT false,   -- IMPORTANTE PARA LGPD
    criado_em             timestamptz NOT NULL DEFAULT now(),
    atualizado_em         timestamptz NOT NULL DEFAULT now()
);


-- INDICES PARA FACILITAR AS QUERYS
CREATE INDEX ix_unidades_bloco             ON unidades (bloco_id);
CREATE INDEX ix_vinculos_unidade           ON vinculos_unidades (unidade_id);
CREATE INDEX ix_avisos_condominio          ON avisos (condominio_id);
CREATE INDEX ix_atas_condominio            ON atas (condominio_id);
CREATE INDEX ix_chamados_condominio        ON chamados (condominio_id, status);
CREATE INDEX ix_chamados_usuario           ON chamados (usuario_id);
CREATE INDEX ix_historicos_chamado         ON historicos_chamados (chamado_id);
CREATE INDEX ix_manutencoes_condominio     ON manutencoes (condominio_id, status);
CREATE INDEX ix_agendamentos_espaco        ON agendamentos_espacos (espaco_comum_id, inicio);
CREATE INDEX ix_classificados_condominio   ON classificados (condominio_id, status);

-- SEGURANCA DO SUPABASE, LIGA AS RLS EM TODAS AS TABELAS
ALTER TABLE condominios           ENABLE ROW LEVEL SECURITY;
ALTER TABLE blocos                ENABLE ROW LEVEL SECURITY;
ALTER TABLE unidades              ENABLE ROW LEVEL SECURITY;
ALTER TABLE areas                 ENABLE ROW LEVEL SECURITY;
ALTER TABLE espacos_comuns        ENABLE ROW LEVEL SECURITY;
ALTER TABLE usuarios              ENABLE ROW LEVEL SECURITY;
ALTER TABLE vinculos_unidades     ENABLE ROW LEVEL SECURITY;
ALTER TABLE papeis_condominios    ENABLE ROW LEVEL SECURITY;
ALTER TABLE contatos_emergencia   ENABLE ROW LEVEL SECURITY;
ALTER TABLE verificacoes_emails   ENABLE ROW LEVEL SECURITY;
ALTER TABLE avisos                ENABLE ROW LEVEL SECURITY;
ALTER TABLE atas                  ENABLE ROW LEVEL SECURITY;
ALTER TABLE chamados              ENABLE ROW LEVEL SECURITY;
ALTER TABLE historicos_chamados   ENABLE ROW LEVEL SECURITY;
ALTER TABLE manutencoes           ENABLE ROW LEVEL SECURITY;
ALTER TABLE agendamentos_espacos  ENABLE ROW LEVEL SECURITY;
ALTER TABLE classificados         ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivos              ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivos_chamados     ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivos_manutencoes  ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivos_classificados ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivos_atas         ENABLE ROW LEVEL SECURITY;
ALTER TABLE leads                 ENABLE ROW LEVEL SECURITY;