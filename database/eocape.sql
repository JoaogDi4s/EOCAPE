-- =============================================================================
-- EOCAPE - Estrutura do banco (schema eocape)
-- Ordem de execução: 01_schema.sql -> 02_seed.sql
-- Plataforma: Supabase (PostgreSQL 15+)
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS eocape;

-- pgcrypto: hash de senhas (bcrypt). No Supabase fica no schema "extensions".
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA extensions;

-- btree_gist: necessária para a constraint de exclusão em agendamento_espaco
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- -----------------------------------------------------------------------------
-- ESTRUTURA FÍSICA
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.condominio (
  id          uuid        NOT NULL DEFAULT gen_random_uuid(),
  nome        text        NOT NULL,
  endereco    text,
  cidade      text,
  uf          varchar(2),
  ativo       boolean     NOT NULL DEFAULT true,
  criado_em   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT condominio_pkey PRIMARY KEY (id)
);

CREATE TABLE eocape.bloco (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  nome            text        NOT NULL,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT bloco_pkey PRIMARY KEY (id),
  CONSTRAINT bloco_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT bloco_condominio_nome_unico UNIQUE (condominio_id, nome)
);

CREATE TABLE eocape.unidade (
  id          uuid        NOT NULL DEFAULT gen_random_uuid(),
  bloco_id    uuid        NOT NULL,
  numero      text        NOT NULL,
  andar       integer,
  criado_em   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT unidade_pkey PRIMARY KEY (id),
  CONSTRAINT unidade_bloco_id_fkey FOREIGN KEY (bloco_id)
    REFERENCES eocape.bloco (id) ON DELETE CASCADE,
  CONSTRAINT unidade_bloco_numero_unico UNIQUE (bloco_id, numero)
);

CREATE TABLE eocape.area (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  nome            text        NOT NULL,
  descricao       text,
  ativo           boolean     NOT NULL DEFAULT true,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT area_pkey PRIMARY KEY (id),
  CONSTRAINT area_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE
);

CREATE TABLE eocape.espaco_comum (
  id                  uuid        NOT NULL DEFAULT gen_random_uuid(),
  area_id             uuid        NOT NULL,
  capacidade          integer     CHECK (capacidade > 0),
  horario_abertura    time,
  horario_fechamento  time,
  taxa                numeric     NOT NULL DEFAULT 0 CHECK (taxa >= 0),
  exige_aprovacao     boolean     NOT NULL DEFAULT false,
  regras              text,
  ativo               boolean     NOT NULL DEFAULT true,
  criado_em           timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT espaco_comum_pkey PRIMARY KEY (id),
  CONSTRAINT espaco_comum_area_id_fkey FOREIGN KEY (area_id)
    REFERENCES eocape.area (id) ON DELETE CASCADE,
  CONSTRAINT espaco_comum_horario_check CHECK (horario_fechamento > horario_abertura)
);

-- -----------------------------------------------------------------------------
-- PESSOAS E ACESSO
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.usuario (
  id                        uuid        NOT NULL DEFAULT gen_random_uuid(),
  cpf                       varchar(11) NOT NULL UNIQUE CHECK (cpf ~ '^[0-9]{11}$'),
  nome                      text        NOT NULL,
  email                     text        NOT NULL UNIQUE,
  telefone                  text        NOT NULL,
  senha_hash                text,
  primeiro_acesso_concluido boolean     NOT NULL DEFAULT false,
  ativo                     boolean     NOT NULL DEFAULT true,
  criado_em                 timestamptz NOT NULL DEFAULT now(),
  atualizado_em             timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT usuario_pkey PRIMARY KEY (id)
);

CREATE TABLE eocape.vinculo_unidade (
  id            uuid        NOT NULL DEFAULT gen_random_uuid(),
  usuario_id    uuid        NOT NULL,
  unidade_id    uuid        NOT NULL,
  tipo_vinculo  text        NOT NULL
    CHECK (tipo_vinculo IN ('PROPRIETARIO', 'INQUILINO', 'DEPENDENTE')),
  principal     boolean     NOT NULL DEFAULT false,
  ativo         boolean     NOT NULL DEFAULT true,
  criado_em     timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT vinculo_unidade_pkey PRIMARY KEY (id),
  CONSTRAINT vinculo_unidade_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id) ON DELETE CASCADE,
  CONSTRAINT vinculo_unidade_unidade_id_fkey FOREIGN KEY (unidade_id)
    REFERENCES eocape.unidade (id) ON DELETE CASCADE,
  CONSTRAINT vinculo_unidade_usuario_unidade_unico UNIQUE (usuario_id, unidade_id)
);

CREATE TABLE eocape.papel_condominio (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  usuario_id      uuid        NOT NULL,
  condominio_id   uuid        NOT NULL,
  papel           text        NOT NULL
    CHECK (papel IN ('MORADOR', 'SINDICO', 'SUBSINDICO', 'PORTEIRO', 'ZELADOR', 'ADMINISTRADOR')),
  ativo           boolean     NOT NULL DEFAULT true,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT papel_condominio_pkey PRIMARY KEY (id),
  CONSTRAINT papel_condominio_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id) ON DELETE CASCADE,
  CONSTRAINT papel_condominio_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT papel_condominio_unico UNIQUE (usuario_id, condominio_id, papel)
);

CREATE TABLE eocape.verificacao_email (
  id            uuid        NOT NULL DEFAULT gen_random_uuid(),
  usuario_id    uuid        NOT NULL,
  email         text        NOT NULL,
  codigo_hash   text        NOT NULL,
  expira_em     timestamptz NOT NULL,
  usado_em      timestamptz,
  tentativas    integer     NOT NULL DEFAULT 0 CHECK (tentativas >= 0),
  criado_em     timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT verificacao_email_pkey PRIMARY KEY (id),
  CONSTRAINT verificacao_email_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- COMUNICAÇÃO E ATAS
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.aviso (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  autor_id        uuid        NOT NULL,
  bloco_id        uuid,
  titulo          text        NOT NULL,
  conteudo        text        NOT NULL,
  importante      boolean     NOT NULL DEFAULT false,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  atualizado_em   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT aviso_pkey PRIMARY KEY (id),
  CONSTRAINT aviso_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT aviso_autor_id_fkey FOREIGN KEY (autor_id)
    REFERENCES eocape.usuario (id),
  CONSTRAINT aviso_bloco_id_fkey FOREIGN KEY (bloco_id)
    REFERENCES eocape.bloco (id) ON DELETE SET NULL
);

CREATE TABLE eocape.ata (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  criado_por_id   uuid        NOT NULL,
  titulo          text        NOT NULL,
  data_reuniao    date        NOT NULL,
  resumo          text,
  publicada       boolean     NOT NULL DEFAULT false,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT ata_pkey PRIMARY KEY (id),
  CONSTRAINT ata_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT ata_criado_por_id_fkey FOREIGN KEY (criado_por_id)
    REFERENCES eocape.usuario (id)
);

-- -----------------------------------------------------------------------------
-- OPERAÇÃO: CHAMADOS E MANUTENÇÃO
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.chamado (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  usuario_id      uuid        NOT NULL,
  unidade_id      uuid,
  area_id         uuid,
  titulo          text        NOT NULL,
  descricao       text        NOT NULL,
  -- TODO: definir lista oficial de categorias e adicionar CHECK
  categoria       text        NOT NULL DEFAULT 'OUTROS',
  status          text        NOT NULL DEFAULT 'ABERTO'
    CHECK (status IN ('ABERTO', 'EM_ANDAMENTO', 'RESOLVIDO', 'CANCELADO')),
  concluido_em    timestamptz,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  atualizado_em   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT chamado_pkey PRIMARY KEY (id),
  CONSTRAINT chamado_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT chamado_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id),
  CONSTRAINT chamado_unidade_id_fkey FOREIGN KEY (unidade_id)
    REFERENCES eocape.unidade (id) ON DELETE SET NULL,
  CONSTRAINT chamado_area_id_fkey FOREIGN KEY (area_id)
    REFERENCES eocape.area (id) ON DELETE SET NULL
);

CREATE TABLE eocape.historico_chamado (
  id                uuid        NOT NULL DEFAULT gen_random_uuid(),
  chamado_id        uuid        NOT NULL,
  usuario_id        uuid        NOT NULL,
  status_anterior   text,
  status_novo       text        NOT NULL
    CHECK (status_novo IN ('ABERTO', 'EM_ANDAMENTO', 'RESOLVIDO', 'CANCELADO')),
  comentario        text,
  criado_em         timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT historico_chamado_pkey PRIMARY KEY (id),
  CONSTRAINT historico_chamado_chamado_id_fkey FOREIGN KEY (chamado_id)
    REFERENCES eocape.chamado (id) ON DELETE CASCADE,
  CONSTRAINT historico_chamado_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id)
);

CREATE TABLE eocape.manutencao (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  unidade_id      uuid,
  area_id         uuid,
  chamado_id      uuid,
  criado_por_id   uuid        NOT NULL,
  titulo          text        NOT NULL,
  descricao       text,
  tipo            text        NOT NULL CHECK (tipo IN ('PREVENTIVA', 'CORRETIVA')),
  status          text        NOT NULL DEFAULT 'AGENDADA'
    CHECK (status IN ('AGENDADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')),
  custo           numeric     CHECK (custo >= 0),
  data_agendada   timestamptz,
  data_conclusao  timestamptz,
  criado_em       timestamptz NOT NULL DEFAULT now(),
  atualizado_em   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT manutencao_pkey PRIMARY KEY (id),
  CONSTRAINT manutencao_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT manutencao_unidade_id_fkey FOREIGN KEY (unidade_id)
    REFERENCES eocape.unidade (id) ON DELETE SET NULL,
  CONSTRAINT manutencao_area_id_fkey FOREIGN KEY (area_id)
    REFERENCES eocape.area (id) ON DELETE SET NULL,
  CONSTRAINT manutencao_chamado_id_fkey FOREIGN KEY (chamado_id)
    REFERENCES eocape.chamado (id) ON DELETE SET NULL,
  CONSTRAINT manutencao_criado_por_id_fkey FOREIGN KEY (criado_por_id)
    REFERENCES eocape.usuario (id)
);

-- -----------------------------------------------------------------------------
-- RESERVAS DE ESPAÇOS COMUNS
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.agendamento_espaco (
  id                uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id     uuid        NOT NULL,
  espaco_comum_id   uuid        NOT NULL,
  usuario_id        uuid        NOT NULL,
  unidade_id        uuid        NOT NULL,
  inicio            timestamptz NOT NULL,
  fim               timestamptz NOT NULL,
  status            text        NOT NULL DEFAULT 'PENDENTE'
    CHECK (status IN ('PENDENTE', 'CONFIRMADO', 'CANCELADO', 'CONCLUIDO')),
  observacao        text,
  criado_em         timestamptz NOT NULL DEFAULT now(),
  atualizado_em     timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT agendamento_espaco_pkey PRIMARY KEY (id),
  CONSTRAINT agendamento_fim_posterior_inicio CHECK (fim > inicio),
  CONSTRAINT agendamento_espaco_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT agendamento_espaco_espaco_comum_id_fkey FOREIGN KEY (espaco_comum_id)
    REFERENCES eocape.espaco_comum (id) ON DELETE CASCADE,
  CONSTRAINT agendamento_espaco_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id),
  CONSTRAINT agendamento_espaco_unidade_id_fkey FOREIGN KEY (unidade_id)
    REFERENCES eocape.unidade (id),
  -- Impede sobreposição de reservas ativas no mesmo espaço (canceladas são ignoradas)
  CONSTRAINT agendamento_sem_sobreposicao EXCLUDE USING gist (
    espaco_comum_id WITH =,
    tstzrange(inicio, fim, '[)') WITH &&
  ) WHERE (status <> 'CANCELADO')
);

-- -----------------------------------------------------------------------------
-- CLASSIFICADOS
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.classificado (
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id   uuid        NOT NULL,
  usuario_id      uuid        NOT NULL,
  titulo          text        NOT NULL,
  descricao       text        NOT NULL,
  tipo            text        NOT NULL CHECK (tipo IN ('VENDA', 'DOACAO', 'SERVICO')),
  preco           numeric     CHECK (preco >= 0),
  status          text        NOT NULL DEFAULT 'ATIVO'
    CHECK (status IN ('ATIVO', 'ENCERRADO')),
  criado_em       timestamptz NOT NULL DEFAULT now(),
  atualizado_em   timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT classificado_pkey PRIMARY KEY (id),
  CONSTRAINT classificado_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT classificado_usuario_id_fkey FOREIGN KEY (usuario_id)
    REFERENCES eocape.usuario (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- ARQUIVOS E TABELAS DE JUNÇÃO
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.arquivo (
  id                    uuid        NOT NULL DEFAULT gen_random_uuid(),
  condominio_id         uuid,
  enviado_por_id        uuid        NOT NULL,
  nome_original         text        NOT NULL,
  chave_armazenamento   text        NOT NULL UNIQUE,
  tipo_mime             text        NOT NULL,
  criado_em             timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT arquivo_pkey PRIMARY KEY (id),
  CONSTRAINT arquivo_condominio_id_fkey FOREIGN KEY (condominio_id)
    REFERENCES eocape.condominio (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_enviado_por_id_fkey FOREIGN KEY (enviado_por_id)
    REFERENCES eocape.usuario (id)
);

CREATE TABLE eocape.arquivo_ata (
  id          uuid NOT NULL DEFAULT gen_random_uuid(),
  ata_id      uuid NOT NULL,
  arquivo_id  uuid NOT NULL,
  CONSTRAINT arquivo_ata_pkey PRIMARY KEY (id),
  CONSTRAINT arquivo_ata_ata_id_fkey FOREIGN KEY (ata_id)
    REFERENCES eocape.ata (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_ata_arquivo_id_fkey FOREIGN KEY (arquivo_id)
    REFERENCES eocape.arquivo (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_ata_unico UNIQUE (ata_id, arquivo_id)
);

CREATE TABLE eocape.arquivo_chamado (
  id           uuid NOT NULL DEFAULT gen_random_uuid(),
  chamado_id   uuid NOT NULL,
  arquivo_id   uuid NOT NULL,
  CONSTRAINT arquivo_chamado_pkey PRIMARY KEY (id),
  CONSTRAINT arquivo_chamado_chamado_id_fkey FOREIGN KEY (chamado_id)
    REFERENCES eocape.chamado (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_chamado_arquivo_id_fkey FOREIGN KEY (arquivo_id)
    REFERENCES eocape.arquivo (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_chamado_unico UNIQUE (chamado_id, arquivo_id)
);

CREATE TABLE eocape.arquivo_classificado (
  id                uuid NOT NULL DEFAULT gen_random_uuid(),
  classificado_id   uuid NOT NULL,
  arquivo_id        uuid NOT NULL,
  CONSTRAINT arquivo_classificado_pkey PRIMARY KEY (id),
  CONSTRAINT arquivo_classificado_classificado_id_fkey FOREIGN KEY (classificado_id)
    REFERENCES eocape.classificado (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_classificado_arquivo_id_fkey FOREIGN KEY (arquivo_id)
    REFERENCES eocape.arquivo (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_classificado_unico UNIQUE (classificado_id, arquivo_id)
);

CREATE TABLE eocape.arquivo_manutencao (
  id              uuid NOT NULL DEFAULT gen_random_uuid(),
  manutencao_id   uuid NOT NULL,
  arquivo_id      uuid NOT NULL,
  CONSTRAINT arquivo_manutencao_pkey PRIMARY KEY (id),
  CONSTRAINT arquivo_manutencao_manutencao_id_fkey FOREIGN KEY (manutencao_id)
    REFERENCES eocape.manutencao (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_manutencao_arquivo_id_fkey FOREIGN KEY (arquivo_id)
    REFERENCES eocape.arquivo (id) ON DELETE CASCADE,
  CONSTRAINT arquivo_manutencao_unico UNIQUE (manutencao_id, arquivo_id)
);

-- -----------------------------------------------------------------------------
-- CAPTAÇÃO (LEADS)
-- -----------------------------------------------------------------------------

CREATE TABLE eocape.lead (
  id                      uuid        NOT NULL DEFAULT gen_random_uuid(),
  nome                    varchar     NOT NULL,
  email                   varchar     NOT NULL,
  telefone                varchar,
  nome_condominio         varchar,
  perfil                  varchar     NOT NULL
    CHECK (perfil IN ('MORADOR', 'SINDICO', 'ADMINISTRADORA', 'OUTRO')),
  consentimento_contato   boolean     NOT NULL DEFAULT false,
  criado_em               timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT lead_pkey PRIMARY KEY (id)
);

-- -----------------------------------------------------------------------------
-- ÍNDICES NAS COLUNAS DE FK (o Postgres não cria automaticamente)
-- -----------------------------------------------------------------------------

CREATE INDEX idx_bloco_condominio         ON eocape.bloco (condominio_id);
CREATE INDEX idx_unidade_bloco            ON eocape.unidade (bloco_id);
CREATE INDEX idx_area_condominio          ON eocape.area (condominio_id);
CREATE INDEX idx_espaco_comum_area        ON eocape.espaco_comum (area_id);
CREATE INDEX idx_vinculo_usuario          ON eocape.vinculo_unidade (usuario_id);
CREATE INDEX idx_vinculo_unidade          ON eocape.vinculo_unidade (unidade_id);
CREATE INDEX idx_papel_condominio         ON eocape.papel_condominio (condominio_id);
CREATE INDEX idx_verificacao_usuario      ON eocape.verificacao_email (usuario_id);
CREATE INDEX idx_aviso_condominio         ON eocape.aviso (condominio_id);
CREATE INDEX idx_aviso_autor              ON eocape.aviso (autor_id);
CREATE INDEX idx_aviso_bloco              ON eocape.aviso (bloco_id);
CREATE INDEX idx_ata_condominio           ON eocape.ata (condominio_id);
CREATE INDEX idx_ata_criado_por           ON eocape.ata (criado_por_id);
CREATE INDEX idx_chamado_condominio       ON eocape.chamado (condominio_id);
CREATE INDEX idx_chamado_usuario          ON eocape.chamado (usuario_id);
CREATE INDEX idx_chamado_unidade          ON eocape.chamado (unidade_id);
CREATE INDEX idx_chamado_area             ON eocape.chamado (area_id);
CREATE INDEX idx_historico_chamado        ON eocape.historico_chamado (chamado_id);
CREATE INDEX idx_historico_usuario        ON eocape.historico_chamado (usuario_id);
CREATE INDEX idx_manutencao_condominio    ON eocape.manutencao (condominio_id);
CREATE INDEX idx_manutencao_unidade       ON eocape.manutencao (unidade_id);
CREATE INDEX idx_manutencao_area          ON eocape.manutencao (area_id);
CREATE INDEX idx_manutencao_chamado       ON eocape.manutencao (chamado_id);
CREATE INDEX idx_manutencao_criado_por    ON eocape.manutencao (criado_por_id);
CREATE INDEX idx_agendamento_condominio   ON eocape.agendamento_espaco (condominio_id);
CREATE INDEX idx_agendamento_usuario      ON eocape.agendamento_espaco (usuario_id);
CREATE INDEX idx_agendamento_unidade      ON eocape.agendamento_espaco (unidade_id);
CREATE INDEX idx_classificado_condominio  ON eocape.classificado (condominio_id);
CREATE INDEX idx_classificado_usuario     ON eocape.classificado (usuario_id);
CREATE INDEX idx_arquivo_condominio       ON eocape.arquivo (condominio_id);
CREATE INDEX idx_arquivo_enviado_por      ON eocape.arquivo (enviado_por_id);
CREATE INDEX idx_arquivo_ata_arquivo      ON eocape.arquivo_ata (arquivo_id);
CREATE INDEX idx_arquivo_chamado_arquivo  ON eocape.arquivo_chamado (arquivo_id);
CREATE INDEX idx_arquivo_classif_arquivo  ON eocape.arquivo_classificado (arquivo_id);
CREATE INDEX idx_arquivo_manut_arquivo    ON eocape.arquivo_manutencao (arquivo_id);

-- -----------------------------------------------------------------------------
-- ROW LEVEL SECURITY
-- Ativado em todas as tabelas. Sem políticas, o acesso via anon/authenticated
-- fica bloqueado. O backend (role postgres) continua acessando normalmente.
-- -----------------------------------------------------------------------------

ALTER TABLE eocape.condominio           ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.bloco                ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.unidade              ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.area                 ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.espaco_comum         ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.usuario              ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.vinculo_unidade      ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.papel_condominio     ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.verificacao_email    ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.aviso                ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.ata                  ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.chamado              ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.historico_chamado    ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.manutencao           ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.agendamento_espaco   ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.classificado         ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.arquivo              ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.arquivo_ata          ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.arquivo_chamado      ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.arquivo_classificado ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.arquivo_manutencao   ENABLE ROW LEVEL SECURITY;
ALTER TABLE eocape.lead                 ENABLE ROW LEVEL SECURITY;

-- Único acesso público permitido: formulário de captação
CREATE POLICY lead_insert_publico
  ON eocape.lead
  FOR INSERT
  TO anon
  WITH CHECK (true);

-- =============================================================================
-- EOCAPE - Dados de teste
-- ATENÇÃO: não executar em produção.
-- CPFs com dígitos verificadores válidos, mas fictícios (não pertencem a pessoas).
-- Senha de todos os usuários de teste: Teste@123 (hash bcrypt via pgcrypto).
-- Rodar depois do 01_schema.sql. O DO é executado como uma transação.
-- Para localizar os registros, use nome, e-mail ou título, não o UUID.
-- =============================================================================

DO $$
DECLARE
  v_senha   text := extensions.crypt('Teste@123', extensions.gen_salt('bf', 10));

  -- Condomínios
  v_cond1    uuid := gen_random_uuid();
  v_cond2    uuid := gen_random_uuid();
  -- Blocos
  v_bloco1a  uuid := gen_random_uuid();
  v_bloco1b  uuid := gen_random_uuid();
  v_bloco2a  uuid := gen_random_uuid();
  -- Unidades
  v_unid1    uuid := gen_random_uuid();
  v_unid2    uuid := gen_random_uuid();
  v_unid3    uuid := gen_random_uuid();
  v_unid4    uuid := gen_random_uuid();
  -- Áreas
  v_area1    uuid := gen_random_uuid();
  v_area2    uuid := gen_random_uuid();
  v_area3    uuid := gen_random_uuid();
  -- Espaços comuns
  v_esp1     uuid := gen_random_uuid();
  v_esp2     uuid := gen_random_uuid();
  v_esp3     uuid := gen_random_uuid();
  -- Usuários
  v_u1       uuid := gen_random_uuid();
  v_u2       uuid := gen_random_uuid();
  v_u3       uuid := gen_random_uuid();
  v_u4       uuid := gen_random_uuid();
  v_u5       uuid := gen_random_uuid();
  v_u6       uuid := gen_random_uuid();
  -- Chamados
  v_cham1    uuid := gen_random_uuid();
  v_cham2    uuid := gen_random_uuid();
  v_cham3    uuid := gen_random_uuid();
  -- Manutenções
  v_manut1   uuid := gen_random_uuid();
  v_manut2   uuid := gen_random_uuid();
  v_manut3   uuid := gen_random_uuid();
  -- Atas
  v_ata1     uuid := gen_random_uuid();
  -- Classificados
  v_classif1 uuid := gen_random_uuid();
  v_classif2 uuid := gen_random_uuid();
  -- Arquivos
  v_arq1     uuid := gen_random_uuid();
  v_arq2     uuid := gen_random_uuid();
  v_arq3     uuid := gen_random_uuid();
  v_arq4     uuid := gen_random_uuid();
BEGIN

-- 1. Condomínios
INSERT INTO eocape.condominio (id, nome, endereco, cidade, uf, ativo) VALUES
  (v_cond1, 'Residencial Jardim das Flores', 'Rua das Acácias, 100', 'Sorocaba', 'SP', true),
  (v_cond2, 'Edifício Vila Nova', 'Av. Paulista, 2000', 'São Paulo', 'SP', true);

-- 2. Blocos
INSERT INTO eocape.bloco (id, condominio_id, nome) VALUES
  (v_bloco1a, v_cond1, 'Bloco A'),
  (v_bloco1b, v_cond1, 'Bloco B'),
  (v_bloco2a, v_cond2, 'Bloco A');

-- 3. Unidades
INSERT INTO eocape.unidade (id, bloco_id, numero, andar) VALUES
  (v_unid1, v_bloco1a, '101', 1),
  (v_unid2, v_bloco1a, '102', 1),
  (v_unid3, v_bloco1b, '201', 2),
  (v_unid4, v_bloco2a, '101', 1);

-- 4. Áreas
INSERT INTO eocape.area (id, condominio_id, nome, descricao, ativo) VALUES
  (v_area1, v_cond1, 'Salão de festas', 'Salão coberto com cozinha', true),
  (v_area2, v_cond1, 'Churrasqueira', 'Área gourmet ao ar livre', true),
  (v_area3, v_cond2, 'Piscina', 'Piscina aquecida com deck', true);

-- 5. Espaços comuns (reserváveis)
INSERT INTO eocape.espaco_comum (id, area_id, capacidade, horario_abertura, horario_fechamento, taxa, exige_aprovacao, regras, ativo) VALUES
  (v_esp1, v_area1, 60, '08:00', '23:00', 150.00, true,  'Proibido som após 22h. Caução de R$ 300.', true),
  (v_esp2, v_area2, 20, '10:00', '22:00',  50.00, false, 'Limpeza por conta do morador.', true),
  (v_esp3, v_area3, 30, '07:00', '20:00',   0.00, false, 'Uso obrigatório de touca.', true);

-- 6. Usuários (6 pessoas de teste)
-- Carla (morador3) ainda não concluiu o primeiro acesso, então não tem senha.
INSERT INTO eocape.usuario (id, cpf, nome, email, telefone, senha_hash, primeiro_acesso_concluido, ativo) VALUES
  (v_u1, '11144477735', 'Roberto Almeida', 'sindico@teste.com',   '(15) 90000-0001', v_senha, true,  true),
  (v_u2, '12345678909', 'Marina Costa',    'morador1@teste.com', '(15) 90000-0002', v_senha, true,  true),
  (v_u3, '98765432100', 'Pedro Santos',    'morador2@teste.com', '(15) 90000-0003', v_senha, true,  true),
  (v_u4, '40123785626', 'José Silva',      'porteiro@teste.com', '(15) 90000-0004', v_senha, true,  true),
  (v_u5, '25869173400', 'Juliana Ramos',   'admin@teste.com',    '(11) 90000-0005', v_senha, true,  true),
  (v_u6, '73648291564', 'Carla Mendes',    'morador3@teste.com', '(11) 90000-0006', NULL,    false, true);

-- 7. Vínculos com unidades
INSERT INTO eocape.vinculo_unidade (usuario_id, unidade_id, tipo_vinculo, principal, ativo) VALUES
  (v_u1, v_unid3, 'PROPRIETARIO', true, true),
  (v_u2, v_unid1, 'PROPRIETARIO', true, true),
  (v_u3, v_unid2, 'INQUILINO',    true, true),
  (v_u6, v_unid4, 'PROPRIETARIO', true, true);

-- 8. Papéis no condomínio
INSERT INTO eocape.papel_condominio (usuario_id, condominio_id, papel, ativo) VALUES
  (v_u1, v_cond1, 'SINDICO',       true),
  (v_u2, v_cond1, 'MORADOR',       true),
  (v_u3, v_cond1, 'MORADOR',       true),
  (v_u4, v_cond1, 'PORTEIRO',      true),
  (v_u5, v_cond2, 'ADMINISTRADOR', true),
  (v_u6, v_cond2, 'MORADOR',       true);

-- 9. Verificações de e-mail
INSERT INTO eocape.verificacao_email (usuario_id, email, codigo_hash, expira_em, usado_em, tentativas) VALUES
  (v_u3, 'morador2@teste.com', extensions.crypt('123456', extensions.gen_salt('bf', 10)), '2026-11-10 12:00:00-03', '2026-11-10 11:30:00-03', 1),
  (v_u6, 'morador3@teste.com', extensions.crypt('654321', extensions.gen_salt('bf', 10)), '2026-11-20 12:00:00-03', NULL, 0);

-- 10. Avisos
INSERT INTO eocape.aviso (condominio_id, autor_id, bloco_id, titulo, conteudo, importante) VALUES
  (v_cond1, v_u1, NULL,
   'Manutenção preventiva do elevador', 'O elevador ficará indisponível na terça-feira, das 8h às 12h, para revisão preventiva.', true),
  (v_cond1, v_u1, v_bloco1a,
   'Coleta seletiva', 'A coleta seletiva do Bloco A passa a ser às quintas-feiras.', false);

-- 11. Atas
INSERT INTO eocape.ata (id, condominio_id, criado_por_id, titulo, data_reuniao, resumo, publicada) VALUES
  (v_ata1, v_cond1, v_u1, 'Assembleia ordinária - outubro/2026', '2026-10-28',
   'Aprovada a prestação de contas do trimestre. Definida a troca de lâmpadas do salão de festas.', true);

-- 12. Chamados
INSERT INTO eocape.chamado (id, condominio_id, usuario_id, unidade_id, area_id, titulo, descricao, categoria, status, concluido_em) VALUES
  (v_cham1, v_cond1, v_u2, v_unid1, NULL,
   'Vazamento na pia', 'Vazamento embaixo da pia da cozinha, água escorrendo para o armário.', 'HIDRAULICA', 'EM_ANDAMENTO', NULL),
  (v_cham2, v_cond1, v_u3, v_unid2, v_area1,
   'Lâmpada queimada no salão', 'Duas lâmpadas do teto do salão de festas estão apagadas.', 'ELETRICA', 'RESOLVIDO', '2026-11-05 16:00:00-03'),
  (v_cham3, v_cond2, v_u6, v_unid4, NULL,
   'Portão da garagem travando', 'O portão da garagem para no meio do percurso e precisa ser empurrado.', 'OUTROS', 'ABERTO', NULL);

-- 13. Histórico dos chamados
INSERT INTO eocape.historico_chamado (chamado_id, usuario_id, status_anterior, status_novo, comentario, criado_em) VALUES
  (v_cham1, v_u1, 'ABERTO',       'EM_ANDAMENTO', 'Encanador agendado para amanhã.', '2026-11-12 09:00:00-03'),
  (v_cham2, v_u1, 'ABERTO',       'EM_ANDAMENTO', 'Eletricista acionado.',           '2026-11-03 10:00:00-03'),
  (v_cham2, v_u1, 'EM_ANDAMENTO', 'RESOLVIDO',    'Lâmpadas trocadas, teste OK.',   '2026-11-05 16:00:00-03');

-- 14. Manutenções
INSERT INTO eocape.manutencao (id, condominio_id, unidade_id, area_id, chamado_id, criado_por_id, titulo, descricao, tipo, status, custo, data_agendada, data_conclusao) VALUES
  (v_manut1, v_cond1, NULL, NULL,   NULL,      v_u1, 'Revisão do elevador', 'Revisão semestral obrigatória pela empresa de manutenção.', 'PREVENTIVA', 'AGENDADA', NULL, '2026-11-24 08:00:00-03', NULL),
  (v_manut2, v_cond1, NULL, v_area1, v_cham2,  v_u1, 'Troca de lâmpadas do salão', 'Substituição de duas lâmpadas LED do teto.', 'CORRETIVA', 'CONCLUIDA', 180.00, '2026-11-05 09:00:00-03', '2026-11-05 15:30:00-03'),
  (v_manut3, v_cond2, NULL, NULL,   v_cham3,   v_u5, 'Reparo do portão da garagem', 'Verificação dos trilhos e do motor do portão.', 'CORRETIVA', 'EM_ANDAMENTO', NULL, '2026-11-17 14:00:00-03', NULL);

-- 15. Agendamentos de espaços
-- O agendamento CANCELADO sobrepõe o primeiro, mas a constraint ignora cancelados.
INSERT INTO eocape.agendamento_espaco (condominio_id, espaco_comum_id, usuario_id, unidade_id, inicio, fim, status, observacao) VALUES
  (v_cond1, v_esp1, v_u2, v_unid1, '2026-11-21 18:00:00-03', '2026-11-21 23:00:00-03', 'CONFIRMADO', 'Aniversário, 40 convidados.'),
  (v_cond1, v_esp1, v_u3, v_unid2, '2026-11-22 10:00:00-03', '2026-11-22 14:00:00-03', 'PENDENTE',   'Reunião de família.'),
  (v_cond1, v_esp1, v_u2, v_unid1, '2026-11-21 18:00:00-03', '2026-11-21 23:00:00-03', 'CANCELADO',  'Cancelado pelo morador.'),
  (v_cond1, v_esp2, v_u3, v_unid2, '2026-11-10 12:00:00-03', '2026-11-10 16:00:00-03', 'CONCLUIDO',  NULL);

-- 16. Classificados
INSERT INTO eocape.classificado (id, condominio_id, usuario_id, titulo, descricao, tipo, preco, status) VALUES
  (v_classif1, v_cond1, v_u2, 'Mesa de jantar de 6 lugares', 'Mesa de madeira maciça, pouco uso, em ótimo estado.', 'VENDA', 350.00, 'ATIVO'),
  (v_classif2, v_cond1, v_u3, 'Aulas particulares de violão', 'Aulas para iniciantes, no apartamento ou no salão do condomínio.', 'SERVICO', NULL, 'ATIVO');

-- 17. Arquivos (metadados; os arquivos físicos ficam no storage)
INSERT INTO eocape.arquivo (id, condominio_id, enviado_por_id, nome_original, chave_armazenamento, tipo_mime) VALUES
  (v_arq1, v_cond1, v_u1, 'ata-2026-10.pdf',   'condo1/atas/' || v_ata1::text || '/ata-2026-10.pdf', 'application/pdf'),
  (v_arq2, v_cond1, v_u2, 'vazamento-pia.jpg', 'condo1/chamados/' || v_cham1::text || '/vazamento-pia.jpg', 'image/jpeg'),
  (v_arq3, v_cond1, v_u2, 'mesa-jantar.jpg',   'condo1/classificados/' || v_classif1::text || '/mesa-jantar.jpg', 'image/jpeg'),
  (v_arq4, v_cond1, v_u1, 'nota-lampadas.pdf', 'condo1/manutencoes/' || v_manut2::text || '/nota-lampadas.pdf', 'application/pdf');

-- 18. Tabelas de junção
INSERT INTO eocape.arquivo_ata (ata_id, arquivo_id) VALUES (v_ata1, v_arq1);
INSERT INTO eocape.arquivo_chamado (chamado_id, arquivo_id) VALUES (v_cham1, v_arq2);
INSERT INTO eocape.arquivo_classificado (classificado_id, arquivo_id) VALUES (v_classif1, v_arq3);
INSERT INTO eocape.arquivo_manutencao (manutencao_id, arquivo_id) VALUES (v_manut2, v_arq4);

-- 19. Leads (captação)
INSERT INTO eocape.lead (nome, email, telefone, nome_condominio, perfil, consentimento_contato) VALUES
  ('Ana Souza',   'ana@teste.com',    '(15) 98888-0001', 'Residencial Jardim das Flores', 'SINDICO',       true),
  ('Carlos Lima', 'carlos@teste.com', NULL,              NULL,                           'ADMINISTRADORA', false);

END $$;