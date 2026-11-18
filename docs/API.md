# Referência dos endpoints

Tabela de consulta rápida. Os controllers em `backend/src/main/java` são a referência dos contratos implementados. Para preparar e executar o projeto, consulte o [README](../README.md).

## Endpoints

A autenticação é feita com `Authorization: Bearer <token>`, exceto onde indicado. Os tokens expiram em 15 minutos.

### Usuário e login

| Método | Rota                           | Quando usar                                                 | Acesso                      |
| ------ | ------------------------------ | ----------------------------------------------------------- | --------------------------- |
| `POST` | `/usuario/login`               | Entrar no sistema. Retorna o token.                         | Público                     |
| `POST` | `/usuario`                     | Cadastrar um morador.                                       | Gestor                      |
| `POST` | `/usuario/definir-senha`       | Definir a senha no primeiro acesso.                         | Token de definição de senha |
| `POST` | `/verificacao-email/codigo`    | Solicitar código de verificação do e-mail.                  | Token de primeiro acesso    |
| `POST` | `/verificacao-email/verificar` | Confirmar o código e receber o token de definição de senha. | Token de primeiro acesso    |

### Estrutura do condomínio

| Método | Rota                            | Quando usar                       | Acesso            |
| ------ | ------------------------------- | --------------------------------- | ----------------- |
| `POST` | `/bloco`                        | Criar um bloco.                   | Gestor            |
| `GET`  | `/bloco/condominio/{id}`        | Listar os blocos.                 | Morador ou gestor |
| `GET`  | `/bloco/{id}`                   | Ver um bloco.                     | Morador ou gestor |
| `POST` | `/unidade`                      | Criar uma unidade em um bloco.    | Gestor            |
| `GET`  | `/unidade/condominio/{id}`      | Listar as unidades.               | Morador ou gestor |
| `GET`  | `/unidade/{id}`                 | Ver uma unidade.                  | Morador ou gestor |
| `POST` | `/area`                         | Criar uma área do prédio.         | Gestor            |
| `GET`  | `/area/condominio/{id}`         | Listar as áreas.                  | Morador ou gestor |
| `GET`  | `/area/{id}`                    | Ver uma área.                     | Morador ou gestor |
| `POST` | `/espaco-comum`                 | Criar um espaço comum reservável. | Gestor            |
| `GET`  | `/espaco-comum/condominio/{id}` | Listar os espaços comuns.         | Morador ou gestor |
| `GET`  | `/espaco-comum/{id}`            | Ver um espaço comum.              | Morador ou gestor |
| `GET`  | `/condominio/meus`              | Listar os condomínios do usuário. | Logado            |
| `GET`  | `/condominio/{id}`              | Ver um condomínio.                | Morador ou gestor |

### Vínculos e papéis

| Método   | Rota                                | Quando usar                            | Acesso         |
| -------- | ----------------------------------- | -------------------------------------- | -------------- |
| `POST`   | `/vinculo-unidade`                  | Ligar um morador a uma unidade.        | Gestor         |
| `GET`    | `/vinculo-unidade/meus`             | Ver as próprias unidades.              | Logado         |
| `GET`    | `/vinculo-unidade/condominio/{id}`  | Ver os vínculos do condomínio.         | Gestor         |
| `GET`    | `/vinculo-unidade/{id}`             | Ver um vínculo.                        | Dono ou gestor |
| `POST`   | `/papel-condominio`                 | Dar papel de síndico ou administrador. | Gestor         |
| `GET`    | `/papel-condominio/meus`            | Ver os próprios papéis.                | Logado         |
| `GET`    | `/papel-condominio/condominio/{id}` | Ver os papéis do condomínio.           | Gestor         |
| `DELETE` | `/papel-condominio/{id}`            | Desativar um papel.                    | Gestor         |

### Atas

| Método   | Rota                                          | Quando usar                                  | Acesso            |
| -------- | --------------------------------------------- | -------------------------------------------- | ----------------- |
| `POST`   | `/api/condominios/{id}/atas`                  | Criar uma ata (rascunho ou já publicada).    | Gestor            |
| `GET`    | `/api/condominios/{id}/atas`                  | Listar as atas. Morador vê só as publicadas. | Morador ou gestor |
| `GET`    | `/api/condominios/{id}/atas/{ataId}`          | Ver uma ata. Rascunho só para gestor.        | Morador ou gestor |
| `PATCH`  | `/api/condominios/{id}/atas/{ataId}/publicar` | Publicar um rascunho.                        | Gestor            |
| `POST`   | `/ata/{ataId}/arquivo`                        | Anexar um arquivo de ata.                    | Gestor            |
| `GET`    | `/ata/{ataId}/arquivo`                        | Ver os anexos da ata.                        | Morador ou gestor |
| `DELETE` | `/ata/{ataId}/arquivo/{vinculoId}`            | Remover um anexo.                            | Gestor            |

### Chamados

| Método   | Rota                                 | Quando usar                                                 | Acesso              |
| -------- | ------------------------------------ | ----------------------------------------------------------- | ------------------- |
| `POST`   | `/chamado`                           | Abrir um chamado para a própria unidade.                    | Morador (ou gestor) |
| `GET`    | `/chamado/condominio/{id}`           | Listar chamados. Morador vê só os próprios.                 | Morador ou gestor   |
| `GET`    | `/chamado/{id}`                      | Ver um chamado.                                             | Dono ou gestor      |
| `PATCH`  | `/chamado/{id}/status`               | Mudar o status (abrir, em andamento, resolvido, cancelado). | Gestor              |
| `GET`    | `/chamado/{id}/historico`            | Ver as mudanças de status.                                  | Dono ou gestor      |
| `POST`   | `/chamado/{id}/arquivos`             | Anexar um arquivo ao chamado.                               | Dono ou gestor      |
| `GET`    | `/chamado/{id}/arquivos`             | Ver os anexos do chamado.                                   | Dono ou gestor      |
| `DELETE` | `/chamado/{id}/arquivos/{vinculoId}` | Remover um anexo.                                           | Dono ou gestor      |

### Manutenções

| Método   | Rota                                    | Quando usar                         | Acesso            |
| -------- | --------------------------------------- | ----------------------------------- | ----------------- |
| `POST`   | `/manutencao`                           | Registrar uma manutenção do prédio. | Gestor            |
| `GET`    | `/manutencao/condominio/{id}`           | Listar as manutenções.              | Morador ou gestor |
| `GET`    | `/manutencao/{id}`                      | Ver uma manutenção.                 | Morador ou gestor |
| `POST`   | `/manutencao/{id}/arquivos`             | Anexar um arquivo à manutenção.     | Gestor            |
| `GET`    | `/manutencao/{id}/arquivos`             | Ver os anexos da manutenção.        | Morador ou gestor |
| `DELETE` | `/manutencao/{id}/arquivos/{vinculoId}` | Remover um anexo.                   | Gestor            |

### Agendamento de espaços

| Método  | Rota                                  | Quando usar                                                  | Acesso              |
| ------- | ------------------------------------- | ------------------------------------------------------------ | ------------------- |
| `POST`  | `/agendamento-espaco`                 | Reservar um espaço comum. Fica pendente se exigir aprovação. | Morador com unidade |
| `GET`   | `/agendamento-espaco/condominio/{id}` | Listar reservas. Morador vê só as próprias.                  | Morador ou gestor   |
| `GET`   | `/agendamento-espaco/{id}`            | Ver uma reserva.                                             | Dono ou gestor      |
| `PATCH` | `/agendamento-espaco/{id}/aprovar`    | Aprovar uma reserva pendente.                                | Gestor              |
| `PATCH` | `/agendamento-espaco/{id}/cancelar`   | Cancelar uma reserva.                                        | Dono ou gestor      |

### Classificados

| Método   | Rota                                      | Quando usar                                          | Acesso            |
| -------- | ----------------------------------------- | ---------------------------------------------------- | ----------------- |
| `POST`   | `/classificado`                           | Anunciar venda, doação ou serviço.                   | Morador           |
| `GET`    | `/classificado/condominio/{id}`           | Listar anúncios. Morador vê os ativos e os próprios. | Morador ou gestor |
| `GET`    | `/classificado/{id}`                      | Ver um anúncio.                                      | Morador ou gestor |
| `PATCH`  | `/classificado/{id}`                      | Editar título, descrição, tipo ou preço.             | Dono              |
| `PATCH`  | `/classificado/{id}/status`               | Encerrar o anúncio.                                  | Dono ou gestor    |
| `POST`   | `/classificado/{id}/arquivos`             | Anexar um arquivo ao anúncio.                        | Dono ou gestor    |
| `GET`    | `/classificado/{id}/arquivos`             | Ver os anexos do anúncio.                            | Dono ou gestor    |
| `DELETE` | `/classificado/{id}/arquivos/{vinculoId}` | Remover um anexo.                                    | Dono ou gestor    |

### Arquivos

| Método | Rota                                        | Quando usar                                                                | Acesso                |
| ------ | ------------------------------------------- | -------------------------------------------------------------------------- | --------------------- |
| `POST` | `/arquivo/upload?condominioId=...&tipo=...` | Enviar um arquivo (tipos: `ATA`, `CHAMADO`, `CLASSIFICADO`, `MANUTENCAO`). | Morador (ata: gestor) |
| `POST` | `/arquivo`                                  | Registrar só os metadados de um arquivo já existente.                      | Morador ou gestor     |
| `GET`  | `/arquivo/condominio/{id}`                  | Listar arquivos. Morador vê só os próprios.                                | Morador ou gestor     |
| `GET`  | `/arquivo/{id}`                             | Ver os dados de um arquivo.                                                | Dono ou gestor        |
| `GET`  | `/arquivo/{id}/download`                    | Gerar link temporário de download.                                         | Dono ou gestor        |

---

## Administração e comunicados

| Método             | Rota                                  | Finalidade                                           |
| ------------------ | ------------------------------------- | ---------------------------------------------------- |
| GET                | `/condominio/{id}/moradores`          | Listar moradores e vínculos do condomínio autorizado |
| POST               | `/condominio/{id}/moradores`          | Cadastrar usuário e vínculo na mesma transação       |
| POST               | `/condominio/{id}/moradores/vincular` | Vincular cadastro existente por CPF e e-mail         |
| GET / POST         | `/condominio/{id}/avisos`             | Consultar ou criar comunicados                       |
| GET / PUT / DELETE | `/condominio/{id}/avisos/{avisoId}`   | Consultar, editar ou excluir um comunicado           |
| POST               | `/usuario/trocar-senha`               | Trocar a senha confirmando a senha atual             |

As ações administrativas dependem de permissão de gestão no condomínio. O download de anexos de atas publicadas também é permitido aos moradores autorizados do condomínio.
