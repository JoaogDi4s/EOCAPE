# Frontend e backend

## Execução local

- Backend: na pasta backend, execute .\mvnw.cmd spring-boot:run com o .env configurado.
- Frontend: na pasta frontend, execute npm start. O proxy encaminha os endpoints da API para localhost:8080.
- Acesse http://localhost:4200 e use o CPF e a senha cadastrados. O primeiro acesso solicita a confirmação do e-mail e a definição da senha.
- Reinicie o servidor Angular após modificar proxy.conf.json.

## Integrações implementadas

Login por CPF, token Bearer, expiração de sessão, perfil, seleção de condomínio, consulta de comunicados, relatos, classificados, atas, reservas e manutenções. Formulários de relatos, classificados e reservas usam os contratos do backend. Contato usa POST /lead, com indicação de envio e confirmação.

### Painel ampliado

- Relatos: detalhes, histórico, anexos e atualização de status com comentário pelo gestor.
- Reservas: aprovação pelo gestor e cancelamento pelo solicitante ou gestor, conforme o status.
- Atas: criação de rascunho ou publicação imediata, publicação posterior e anexos.
- Comunicados: criação, edição, destaque e exclusão com confirmação pelo gestor.
- Manutenções: cadastro em área ou unidade, consulta e anexos.
- Classificados: edição pelo autor, encerramento pelo autor ou gestor e anexos.
- Configurações: troca de senha usando a API existente.
- Administração: `/user-page/dashboard`, `/user-page/moradores` e `/user-page/estrutura`.
- Dashboard: indicadores, distribuição dos status de relatos e pendências do condomínio selecionado. Falhas de consulta aparecem como informação indisponível, sem serem apresentadas como zero.
- Moradores: lista agrupada por pessoa, unidades e vínculos ativos/inativos, contatos, busca e filtro por primeiro acesso; cadastro com vínculo atômico à unidade.
- Estrutura e acessos: cadastro de blocos, unidades, áreas e espaços reserváveis; atribuição e revogação de papéis.

Os menus e as rotas administrativas verificam papéis ativos. A seleção do condomínio é preservada na sessão e validada contra os condomínios permitidos em cada tela. As ações de gestão também são protegidas pelo backend. A listagem de moradores utiliza `GET /condominio/{id}/moradores`, sem consultar a lista global de usuários. O cadastro utiliza `POST` na mesma rota, com `{ usuario: CadastroUsuarioRequest, unidadeId }`; cadastro e vínculo participam da mesma transação.

O endpoint de anexos de atas usa `/ata/{id}/arquivo` (singular), conforme o controller atual. Os demais módulos usam `/arquivos`. Moradores vinculados podem baixar anexos de atas publicadas do mesmo condomínio; rascunhos e arquivos privados de outras pessoas continuam protegidos. O backend verifica a associação real entre arquivo e ata publicada antes de gerar o link temporário. O frontend baixa o conteúdo pelo link assinado e preserva o nome original, sem depender de abertura automática de pop-ups. Não houve alteração nas tabelas do banco.

### UX e fotos nos chamados

O painel usa superfícies claras, tipografia mais legível, contraste nos controles, estados de foco e validação junto aos formulários. O primeiro acesso apresenta etapas, requisitos da senha, opção de mostrar senhas e reenvio de código. Mensagens da API incluem os campos inválidos e orientações para arquivos grandes, sessão expirada e indisponibilidade.

Ao criar um chamado, é possível selecionar até cinco imagens de até 10 MB cada, visualizar e remover antes de enviar. Em telas de até 768 px, o botão `Tirar foto` usa `accept="image/*" capture="environment"`; a câmera nativa depende do dispositivo/navegador. A galeria permanece disponível em qualquer largura. Nos detalhes do chamado, o mesmo controle permite fotografar e anexar diretamente.

O chamado é criado antes do envio das imagens. Se uma foto falhar, o chamado salvo é preservado e a tela permite reenviar apenas os anexos pendentes. Se o upload foi concluído e apenas a associação falhou, o reenvio reutiliza o arquivo enviado. Fechar a tela com anexos pendentes descarta as prévias locais; arquivos já enviados ao storage podem permanecer sem associação. A captura por hardware ainda precisa ser conferida em um celular real; o navegador de testes validou seleção, prévia, envio simulado e responsividade em 320/375/768/1440 px.

No formulário de moradores, `Vincular cadastro existente` utiliza `POST /condominio/{id}/moradores/vincular` com CPF, e-mail e unidade. O backend exige gestão do condomínio de destino, confere CPF e e-mail do cadastro ativo e impede vínculo ativo duplicado. O vínculo principal anterior e a senha são preservados. O cadastro novo continua rejeitando CPF ou e-mail duplicados, com mensagem específica exibida na tela; nenhum cadastro é sobrescrito automaticamente.

Os dois fluxos enviam também `tipoVinculo`, selecionado entre `PROPRIETARIO`, `INQUILINO` e `DEPENDENTE`, conforme o CHECK de `eocape.vinculo_unidade`. Clientes antigos que não enviam esse campo usam `INQUILINO` por compatibilidade. `MORADOR` é um papel, não um tipo de vínculo. A validação do serviço impede tipos inválidos antes do INSERT; erros de integridade não são mais rotulados automaticamente como conflito de vínculo principal.

Sugestões e encomendas continuam como prévias visuais porque não existem endpoints correspondentes. Preferências são locais ao navegador; não ativam notificações. O perfil é somente leitura, pois não existe atualização de cadastro na API atual.

## Validação

As nove páginas do painel foram verificadas em 375, 768 e 1440 pixels sem transbordamento horizontal. Login válido e inválido foram testados no navegador com a conta de teste documentada em backend/http/testes.http. As seis consultas principais retornaram HTTP 200 pelo proxy. O backend conectou ao Supabase.

Na ampliação do painel, o build de produção foi validado e os testes incluem escopo administrativo por condomínio, métricas indisponíveis, paginação de comunicados, autenticação das rotas administrativas, aprovação de reservas e contrato de anexos de atas. Os testes específicos do backend cobrem a negação de acesso à lista de moradores e a rejeição de cadastro em unidade de outro condomínio.

A revisão visual da ampliação usou dados sintéticos em servidor local, com larguras de 375, 768 e 1440 pixels. Foram conferidos dashboard, moradores, formulário de cadastro, detalhes de relatos e estrutura. Não foram criados moradores, alterados papéis ou enviados arquivos ao banco real nessa validação. E-mail, primeiro acesso e integração de escrita com Supabase precisam de validação em ambiente de homologação.

## Hospedagem

O proxy do Angular funciona apenas no servidor de desenvolvimento. Em produção, configure o servidor web para encaminhar os mesmos caminhos de proxy.conf.json ao backend e devolver index.html para as rotas do frontend. Não publique o .env nem chaves do Supabase no frontend.
