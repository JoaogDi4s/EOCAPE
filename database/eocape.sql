-- ============================================================
-- EOCAPE - versão simplificada, nomenclatura compatível com Spring Boot
--
-- Regras de nomes usadas aqui:
--  * tabelas no SINGULAR e em snake_case (entidade EspacoComum -> espaco_comum)
--  * colunas em snake_case (campo criadoEm -> criado_em)
--  * toda chave estrangeira termina em _id (campo autor -> autor_id)
--  * valores de status/tipo em MAIÚSCULAS, iguais às constantes do enum Java
--    (@Enumerated(EnumType.STRING))
--  * varchar no lugar de char(n), para o Hibernate validar sem erro
--
-- ATENÇÃO: o DROP SCHEMA apaga TODOS os dados do schema eocape.
-- Use só em desenvolvimento.
-- ============================================================

-- DROP SCHEMA IF EXISTS eocape CASCADE;
-- CREATE SCHEMA eocape;
-- SET search_path TO eocape, public;

-- ------------------------------------------------------------
-- ESTRUTURA DO CONDOMÍNIO
-- ------------------------------------------------------------
CREATE TABLE condominio (
    id        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome      text NOT NULL,
    endereco  text,
    cidade    text,
    uf        varchar(2),
    ativo     boolean NOT NULL DEFAULT true,
    criado_em timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE bloco (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    nome          text NOT NULL,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (condominio_id, nome)
);

-- Apartamentos
CREATE TABLE unidade (
    id        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bloco_id  uuid NOT NULL REFERENCES bloco(id),
    numero    text NOT NULL,
    andar     integer,
    criado_em timestamptz NOT NULL DEFAULT now(),
    UNIQUE (bloco_id, numero)
);

-- Lugares do condomínio em geral
CREATE TABLE area (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    nome          text NOT NULL,
    descricao     text,
    ativo         boolean NOT NULL DEFAULT true,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (condominio_id, nome)
);

-- Áreas que podem ser reservadas (salão, churrasqueira, quadra)
CREATE TABLE espaco_comum (
    id                 uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    area_id            uuid NOT NULL UNIQUE REFERENCES area(id),
    capacidade         integer,
    horario_abertura   time,
    horario_fechamento time,
    taxa               numeric(10,2) NOT NULL DEFAULT 0,
    exige_aprovacao    boolean NOT NULL DEFAULT false,
    regras             text,
    ativo              boolean NOT NULL DEFAULT true,
    criado_em          timestamptz NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- USUÁRIOS
-- ------------------------------------------------------------
CREATE TABLE usuario (
    id                        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf                       varchar(11) NOT NULL UNIQUE,
    nome                      text NOT NULL,
    email                     text UNIQUE,
    telefone                  text,
    senha_hash                text NOT NULL,
    primeiro_acesso_concluido boolean NOT NULL DEFAULT false,
    ativo                     boolean NOT NULL DEFAULT true,
    criado_em                 timestamptz NOT NULL DEFAULT now(),
    atualizado_em             timestamptz NOT NULL DEFAULT now()
);

-- Liga o usuário ao(s) seu(s) apartamento(s). ativo = false quando sai da unidade
CREATE TABLE vinculo_unidade (
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id   uuid NOT NULL REFERENCES usuario(id),
    unidade_id   uuid NOT NULL REFERENCES unidade(id),
    tipo_vinculo text NOT NULL CHECK (tipo_vinculo IN ('PROPRIETARIO', 'INQUILINO', 'DEPENDENTE')),
    principal    boolean NOT NULL DEFAULT false,
    ativo        boolean NOT NULL DEFAULT true,
    criado_em    timestamptz NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, unidade_id)
);

-- Papel do usuário no condomínio
CREATE TABLE papel_condominio (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id    uuid NOT NULL REFERENCES usuario(id),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    papel         text NOT NULL CHECK (papel IN ('MORADOR', 'SINDICO', 'SUBSINDICO', 'PORTEIRO', 'ZELADOR')),
    ativo         boolean NOT NULL DEFAULT true,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (usuario_id, condominio_id, papel)
);

-- Códigos do primeiro acesso
CREATE TABLE verificacao_email (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id  uuid NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    email       text NOT NULL,
    codigo_hash text NOT NULL,
    expira_em   timestamptz NOT NULL,
    usado_em    timestamptz,
    tentativas  integer NOT NULL DEFAULT 0,
    criado_em   timestamptz NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- COMUNICAÇÃO
-- ------------------------------------------------------------
-- bloco_id vazio = aviso para todos
CREATE TABLE aviso (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    autor_id      uuid NOT NULL REFERENCES usuario(id),
    bloco_id      uuid REFERENCES bloco(id),
    titulo        text NOT NULL,
    conteudo      text NOT NULL,
    importante    boolean NOT NULL DEFAULT false,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE ata (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    criado_por_id uuid NOT NULL REFERENCES usuario(id),
    titulo        text NOT NULL,
    data_reuniao  date NOT NULL,
    resumo        text,
    criado_em     timestamptz NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- CHAMADOS E MANUTENÇÕES
-- ------------------------------------------------------------
CREATE TABLE chamado (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    usuario_id    uuid NOT NULL REFERENCES usuario(id),
    unidade_id    uuid REFERENCES unidade(id),
    area_id       uuid REFERENCES area(id),
    titulo        text NOT NULL,
    descricao     text NOT NULL,
    categoria     text NOT NULL DEFAULT 'OUTROS',
    status        text NOT NULL DEFAULT 'ABERTO'
                  CHECK (status IN ('ABERTO', 'EM_ANDAMENTO', 'RESOLVIDO', 'CANCELADO')),
    concluido_em  timestamptz,
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);

-- Cada troca de status do chamado
CREATE TABLE historico_chamado (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    chamado_id      uuid NOT NULL REFERENCES chamado(id) ON DELETE CASCADE,
    usuario_id      uuid NOT NULL REFERENCES usuario(id),
    status_anterior text,
    status_novo     text NOT NULL,
    comentario      text,
    criado_em       timestamptz NOT NULL DEFAULT now()
);

-- Ou é de uma unidade ou é de uma área (nunca as duas)
CREATE TABLE manutencao (
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id  uuid NOT NULL REFERENCES condominio(id),
    unidade_id     uuid REFERENCES unidade(id),
    area_id        uuid REFERENCES area(id),
    chamado_id     uuid REFERENCES chamado(id),
    criado_por_id  uuid NOT NULL REFERENCES usuario(id),
    titulo         text NOT NULL,
    descricao      text,
    tipo           text NOT NULL CHECK (tipo IN ('PREVENTIVA', 'CORRETIVA')),
    status         text NOT NULL DEFAULT 'AGENDADA'
                   CHECK (status IN ('AGENDADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')),
    custo          numeric(10,2),
    data_agendada  timestamptz,
    data_conclusao timestamptz,
    criado_em      timestamptz NOT NULL DEFAULT now(),
    atualizado_em  timestamptz NOT NULL DEFAULT now(),
    CHECK ((unidade_id IS NOT NULL AND area_id IS NULL)
        OR (unidade_id IS NULL AND area_id IS NOT NULL))
);

-- ------------------------------------------------------------
-- AGENDAMENTOS E CLASSIFICADOS
-- ------------------------------------------------------------
CREATE TABLE agendamento_espaco (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id   uuid NOT NULL REFERENCES condominio(id),
    espaco_comum_id uuid NOT NULL REFERENCES espaco_comum(id),
    usuario_id      uuid NOT NULL REFERENCES usuario(id),
    unidade_id      uuid NOT NULL REFERENCES unidade(id),
    inicio          timestamptz NOT NULL,
    fim             timestamptz NOT NULL,
    status          text NOT NULL DEFAULT 'PENDENTE'
                    CHECK (status IN ('PENDENTE', 'CONFIRMADO', 'CANCELADO', 'CONCLUIDO')),
    observacao      text,
    criado_em       timestamptz NOT NULL DEFAULT now(),
    atualizado_em   timestamptz NOT NULL DEFAULT now(),
    CHECK (fim > inicio)
);

CREATE TABLE classificado (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id uuid NOT NULL REFERENCES condominio(id),
    usuario_id    uuid NOT NULL REFERENCES usuario(id),
    titulo        text NOT NULL,
    descricao     text NOT NULL,
    tipo          text NOT NULL CHECK (tipo IN ('VENDA', 'DOACAO', 'SERVICO')),
    preco         numeric(10,2),
    status        text NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO', 'ENCERRADO')),
    criado_em     timestamptz NOT NULL DEFAULT now(),
    atualizado_em timestamptz NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- ARQUIVOS (fotos, vídeos e documentos guardados no Supabase Storage)
-- ------------------------------------------------------------
CREATE TABLE arquivo (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    condominio_id       uuid REFERENCES condominio(id),
    enviado_por_id      uuid NOT NULL REFERENCES usuario(id),
    nome_original       text NOT NULL,
    chave_armazenamento text NOT NULL UNIQUE,
    tipo_mime           text NOT NULL,
    criado_em           timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE arquivo_chamado (
    chamado_id uuid NOT NULL REFERENCES chamado(id) ON DELETE CASCADE,
    arquivo_id uuid NOT NULL REFERENCES arquivo(id) ON DELETE CASCADE,
    PRIMARY KEY (chamado_id, arquivo_id)
);

CREATE TABLE arquivo_manutencao (
    manutencao_id uuid NOT NULL REFERENCES manutencao(id) ON DELETE CASCADE,
    arquivo_id    uuid NOT NULL REFERENCES arquivo(id) ON DELETE CASCADE,
    PRIMARY KEY (manutencao_id, arquivo_id)
);

CREATE TABLE arquivo_classificado (
    classificado_id uuid NOT NULL REFERENCES classificado(id) ON DELETE CASCADE,
    arquivo_id      uuid NOT NULL REFERENCES arquivo(id) ON DELETE CASCADE,
    PRIMARY KEY (classificado_id, arquivo_id)
);

CREATE TABLE arquivo_ata (
    ata_id     uuid NOT NULL REFERENCES ata(id) ON DELETE CASCADE,
    arquivo_id uuid NOT NULL REFERENCES arquivo(id) ON DELETE CASCADE,
    PRIMARY KEY (ata_id, arquivo_id)
);

-- ------------------------------------------------------------
-- LEADS (formulário "Entre em contato")
-- ------------------------------------------------------------
CREATE TABLE lead (
    id                    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                  text NOT NULL,
    email                 text NOT NULL,
    telefone              text,
    nome_condominio       text,
    perfil                text CHECK (perfil IN ('SINDICO', 'MORADOR', 'ADMINISTRADORA', 'OUTRO')),
    consentimento_contato boolean NOT NULL DEFAULT false,  -- LGPD
    criado_em             timestamptz NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- ÍNDICES (só os essenciais)
-- ------------------------------------------------------------
CREATE INDEX ix_vinculo_unidade_unidade    ON vinculo_unidade (unidade_id);
CREATE INDEX ix_aviso_condominio           ON aviso (condominio_id);
CREATE INDEX ix_chamado_condominio         ON chamado (condominio_id, status);
CREATE INDEX ix_agendamento_espaco_comum   ON agendamento_espaco (espaco_comum_id, inicio);

-- ------------------------------------------------------------
-- SEGURANÇA DO SUPABASE: liga o RLS em todas as tabelas
-- (o backend conecta como postgres e não é afetado)
-- ------------------------------------------------------------
ALTER TABLE condominio           ENABLE ROW LEVEL SECURITY;
ALTER TABLE bloco                ENABLE ROW LEVEL SECURITY;
ALTER TABLE unidade              ENABLE ROW LEVEL SECURITY;
ALTER TABLE area                 ENABLE ROW LEVEL SECURITY;
ALTER TABLE espaco_comum         ENABLE ROW LEVEL SECURITY;
ALTER TABLE usuario              ENABLE ROW LEVEL SECURITY;
ALTER TABLE vinculo_unidade      ENABLE ROW LEVEL SECURITY;
ALTER TABLE papel_condominio     ENABLE ROW LEVEL SECURITY;
ALTER TABLE verificacao_email    ENABLE ROW LEVEL SECURITY;
ALTER TABLE aviso                ENABLE ROW LEVEL SECURITY;
ALTER TABLE ata                  ENABLE ROW LEVEL SECURITY;
ALTER TABLE chamado              ENABLE ROW LEVEL SECURITY;
ALTER TABLE historico_chamado    ENABLE ROW LEVEL SECURITY;
ALTER TABLE manutencao           ENABLE ROW LEVEL SECURITY;
ALTER TABLE agendamento_espaco   ENABLE ROW LEVEL SECURITY;
ALTER TABLE classificado         ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivo              ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivo_chamado      ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivo_manutencao   ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivo_classificado ENABLE ROW LEVEL SECURITY;
ALTER TABLE arquivo_ata          ENABLE ROW LEVEL SECURITY;
ALTER TABLE lead                 ENABLE ROW LEVEL SECURITY;