# EOCAPE: Backend

Sistema de gestão para condomínios. Permite que síndicos e administradores gerenciem a estrutura do prédio, e que moradores acompanhem avisos, abram chamados, reservem espaços, anunciem itens e consultem arquivos.

---

## Sumário

1. [Principais funcionalidades](#principais-funcionalidades)
2. [Como funciona o acesso](#como-funciona-o-acesso)
3. [Como rodar o projeto](#como-rodar-o-projeto)
4. [Endpoints](#endpoints)
5. [Testes](#testes)

---

## Principais funcionalidades

- **Cadastro e login** de moradores, com verificação de e-mail no primeiro acesso.
- **Estrutura do prédio**: blocos, unidades, áreas e espaços comuns.
- **Vínculos**: liga cada morador às unidades que ele ocupa.
- **Atas de assembleia**: rascunho e publicação, com anexos.
- **Chamados**: moradores pedem reparos ou serviços, com histórico de status e anexos.
- **Manutenções**: ações do prédio criadas pelo síndico, com anexos.
- **Reserva de espaços**: moradores reservam áreas comuns, com aprovação quando necessário.
- **Classificados**: anúncios de venda, doação ou serviço entre vizinhos.
- **Arquivos**: upload para armazenamento privado, com download por link temporário.

---

## Como funciona o acesso

Cada usuário tem um **papel** dentro do condomínio. Os papéis determinam o que ele pode fazer.

| Papel | O que pode |
|---|---|
| **Morador** (vínculo ativo em uma unidade) | Ver o que é do condomínio, abrir chamados e reservas, anunciar, enviar arquivos |
| **Síndico / Administrador** (papel ativo) | Tudo do morador, mais gerenciar a estrutura, publicar atas, aprovar reservas e atualizar status |

Regras que valem em todo o sistema:

- Um usuário só acessa dados do condomínio em que tem vínculo ou papel ativo.
- Rascunhos de atas e anúncios encerrados não aparecem para quem não é dono nem gestor.
- Arquivos só são baixados por quem os enviou ou por um gestor, por link que expira.

---

## Como rodar o projeto

### Pré-requisitos

- Java 25
- Maven (o projeto já traz o `mvnw`)
- Conta no Supabase (banco PostgreSQL e Storage)
- Conta no Mailtrap (opcional, para testar e-mail)

### 1. Configurar o `.env`

Crie um arquivo `.env` na pasta `backend`, com base no `.env.example`:

```dotenv
DB_URL=jdbc:postgresql://HOST_DO_SUPABASE:5432/postgres
DB_USER=postgres.SEU_PROJETO_REF
DB_PASSWORD=sua_senha

JWT_SECRET=uma_chave_com_pelo_menos_32_caracteres

MAIL_FROM=noreply@eocape.test
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=587
MAIL_USERNAME=seu_usuario_mailtrap
MAIL_PASSWORD=sua_senha_mailtrap

SUPABASE_URL=https://SEU_PROJETO_REF.supabase.co
SUPABASE_SERVICE_KEY=sua_secret_key
SUPABASE_BUCKET=eocape
```

Nunca envie o `.env` para o repositório. Ele deve estar no `.gitignore`.

### 2. Preparar o banco

O schema `eocape` precisa existir, com as tabelas do projeto. Confira no Supabase que as tabelas aparecem no schema `eocape`.

### 3. Criar o storage

No Supabase, crie um bucket chamado `eocape` e deixe-o **privado**.

### 4. Subir a aplicação

```powershell
.\mvnw clean spring-boot:run
```

A aplicação responde em `http://localhost:8080`. Quando aparecer `Started BackendApplication`, está pronta.

### 5. Criar o primeiro síndico

O primeiro síndico não pode ser criado pela API, porque a criação exige um gestor logado. Insira-o pelo SQL, com um hash BCrypt de senha gerado pelo projeto (veja o passo de seed no histórico do projeto).

---

## Endpoints

A autenticação é feita com `Authorization: Bearer <token>`, exceto onde indicado. Os tokens expiram em 15 minutos.

### Usuário e login

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/usuario/login` | Entrar no sistema. Retorna o token. | Público |
| `POST` | `/usuario` | Cadastrar um morador. | Gestor |
| `POST` | `/usuario/definir-senha` | Definir a senha no primeiro acesso. | Token de definição de senha |
| `POST` | `/verificacao-email/codigo` | Solicitar código de verificação do e-mail. | Token de primeiro acesso |
| `POST` | `/verificacao-email/verificar` | Confirmar o código e receber o token de definição de senha. | Token de primeiro acesso |

### Estrutura do condomínio

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/bloco` | Criar um bloco. | Gestor |
| `GET` | `/bloco/condominio/{id}` | Listar os blocos. | Morador ou gestor |
| `GET` | `/bloco/{id}` | Ver um bloco. | Morador ou gestor |
| `POST` | `/unidade` | Criar uma unidade em um bloco. | Gestor |
| `GET` | `/unidade/condominio/{id}` | Listar as unidades. | Morador ou gestor |
| `GET` | `/unidade/{id}` | Ver uma unidade. | Morador ou gestor |
| `POST` | `/area` | Criar uma área do prédio. | Gestor |
| `GET` | `/area/condominio/{id}` | Listar as áreas. | Morador ou gestor |
| `GET` | `/area/{id}` | Ver uma área. | Morador ou gestor |
| `POST` | `/espaco-comum` | Criar um espaço comum reservável. | Gestor |
| `GET` | `/espaco-comum/condominio/{id}` | Listar os espaços comuns. | Morador ou gestor |
| `GET` | `/espaco-comum/{id}` | Ver um espaço comum. | Morador ou gestor |
| `GET` | `/condominio/meus` | Listar os condomínios do usuário. | Logado |
| `GET` | `/condominio/{id}` | Ver um condomínio. | Morador ou gestor |

### Vínculos e papéis

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/vinculo-unidade` | Ligar um morador a uma unidade. | Gestor |
| `GET` | `/vinculo-unidade/meus` | Ver as próprias unidades. | Logado |
| `GET` | `/vinculo-unidade/condominio/{id}` | Ver os vínculos do condomínio. | Gestor |
| `GET` | `/vinculo-unidade/{id}` | Ver um vínculo. | Dono ou gestor |
| `POST` | `/papel-condominio` | Dar papel de síndico ou administrador. | Gestor |
| `GET` | `/papel-condominio/meus` | Ver os próprios papéis. | Logado |
| `GET` | `/papel-condominio/condominio/{id}` | Ver os papéis do condomínio. | Gestor |
| `DELETE` | `/papel-condominio/{id}` | Desativar um papel. | Gestor |

### Atas

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/api/condominios/{id}/atas` | Criar uma ata (rascunho ou já publicada). | Gestor |
| `GET` | `/api/condominios/{id}/atas` | Listar as atas. Morador vê só as publicadas. | Morador ou gestor |
| `GET` | `/api/condominios/{id}/atas/{ataId}` | Ver uma ata. Rascunho só para gestor. | Morador ou gestor |
| `PATCH` | `/api/condominios/{id}/atas/{ataId}/publicar` | Publicar um rascunho. | Gestor |
| `POST` | `/ata/{ataId}/arquivos` | Anexar um arquivo de ata. | Gestor |
| `GET` | `/ata/{ataId}/arquivos` | Ver os anexos da ata. | Morador ou gestor |
| `DELETE` | `/ata/{ataId}/arquivos/{vinculoId}` | Remover um anexo. | Gestor |

### Chamados

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/chamado` | Abrir um chamado para a própria unidade. | Morador (ou gestor) |
| `GET` | `/chamado/condominio/{id}` | Listar chamados. Morador vê só os próprios. | Morador ou gestor |
| `GET` | `/chamado/{id}` | Ver um chamado. | Dono ou gestor |
| `PATCH` | `/chamado/{id}/status` | Mudar o status (abrir, em andamento, resolvido, cancelado). | Gestor |
| `GET` | `/chamado/{id}/historico` | Ver as mudanças de status. | Dono ou gestor |
| `POST` | `/chamado/{id}/arquivos` | Anexar um arquivo ao chamado. | Dono ou gestor |
| `GET` | `/chamado/{id}/arquivos` | Ver os anexos do chamado. | Dono ou gestor |
| `DELETE` | `/chamado/{id}/arquivos/{vinculoId}` | Remover um anexo. | Dono ou gestor |

### Manutenções

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/manutencao` | Registrar uma manutenção do prédio. | Gestor |
| `GET` | `/manutencao/condominio/{id}` | Listar as manutenções. | Morador ou gestor |
| `GET` | `/manutencao/{id}` | Ver uma manutenção. | Morador ou gestor |
| `POST` | `/manutencao/{id}/arquivos` | Anexar um arquivo à manutenção. | Gestor |
| `GET` | `/manutencao/{id}/arquivos` | Ver os anexos da manutenção. | Morador ou gestor |
| `DELETE` | `/manutencao/{id}/arquivos/{vinculoId}` | Remover um anexo. | Gestor |

### Agendamento de espaços

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/agendamento-espaco` | Reservar um espaço comum. Fica pendente se exigir aprovação. | Morador com unidade |
| `GET` | `/agendamento-espaco/condominio/{id}` | Listar reservas. Morador vê só as próprias. | Morador ou gestor |
| `GET` | `/agendamento-espaco/{id}` | Ver uma reserva. | Dono ou gestor |
| `PATCH` | `/agendamento-espaco/{id}/aprovar` | Aprovar uma reserva pendente. | Gestor |
| `PATCH` | `/agendamento-espaco/{id}/cancelar` | Cancelar uma reserva. | Dono ou gestor |

### Classificados

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/classificado` | Anunciar venda, doação ou serviço. | Morador |
| `GET` | `/classificado/condominio/{id}` | Listar anúncios. Morador vê os ativos e os próprios. | Morador ou gestor |
| `GET` | `/classificado/{id}` | Ver um anúncio. | Morador ou gestor |
| `PATCH` | `/classificado/{id}` | Editar título, descrição, tipo ou preço. | Dono |
| `PATCH` | `/classificado/{id}/status` | Encerrar o anúncio. | Dono ou gestor |
| `POST` | `/classificado/{id}/arquivos` | Anexar um arquivo ao anúncio. | Dono ou gestor |
| `GET` | `/classificado/{id}/arquivos` | Ver os anexos do anúncio. | Dono ou gestor |
| `DELETE` | `/classificado/{id}/arquivos/{vinculoId}` | Remover um anexo. | Dono ou gestor |

### Arquivos

| Método | Rota | Quando usar | Acesso |
|---|---|---|---|
| `POST` | `/arquivo/upload?condominioId=...&tipo=...` | Enviar um arquivo (tipos: `ATA`, `CHAMADO`, `CLASSIFICADO`, `MANUTENCAO`). | Morador (ata: gestor) |
| `POST` | `/arquivo` | Registrar só os metadados de um arquivo já existente. | Morador ou gestor |
| `GET` | `/arquivo/condominio/{id}` | Listar arquivos. Morador vê só os próprios. | Morador ou gestor |
| `GET` | `/arquivo/{id}` | Ver os dados de um arquivo. | Dono ou gestor |
| `GET` | `/arquivo/{id}/download` | Gerar link temporário de download. | Dono ou gestor |

---

## Testes

Os testes estão em arquivos `.http`, que rodam no VS Code com a extensão **REST Client**:

1. Instale a extensão REST Client.
2. Abra o arquivo `.http` do módulo que quer testar.
3. Preencha as variáveis do topo (ids e senhas).
4. Rode os logins da seção 1 e copie os tokens.
5. Execute as seções na ordem. Cada requisição indica o status esperado no comentário.

Os tokens expiram em 15 minutos. Se um teste der 401 depois de um tempo, refaça o login.

Para os testes de upload, crie `teste.jpg` e `teste.pdf` na mesma pasta do arquivo `.http`.