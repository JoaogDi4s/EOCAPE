# Frontend e backend

## Execução local

- Backend: na pasta backend, execute .\mvnw.cmd spring-boot:run com o .env configurado.
- Frontend: na pasta frontend, execute npm start. O proxy encaminha os endpoints da API para localhost:8080.
- Acesse http://localhost:4200 e use o CPF e a senha cadastrados. O primeiro acesso solicita a confirmação do e-mail e a definição da senha.
- Reinicie o servidor Angular após modificar proxy.conf.json.

## Integrações implementadas

Login por CPF, token Bearer, expiração de sessão, perfil, seleção de condomínio, consulta de comunicados, relatos, classificados, atas, reservas e manutenções. Formulários de relatos, classificados e reservas usam os contratos do backend. Contato usa POST /lead, com indicação de envio e confirmação.

Sugestões e encomendas continuam como prévias visuais porque não existem endpoints correspondentes. Preferências são locais ao navegador; não ativam notificações. O perfil é somente leitura, pois não existe atualização de cadastro na API atual.

## Validação

As nove páginas do painel foram verificadas em 375, 768 e 1440 pixels sem transbordamento horizontal. Login válido e inválido foram testados no navegador com a conta de teste documentada em backend/http/testes.http. As seis consultas principais retornaram HTTP 200 pelo proxy. O backend conectou ao Supabase.

32 testes do frontend e um teste específico do backend passaram. Os fluxos de escrita possuem testes de contrato e validação; não foram criados registros no banco durante a revisão. Envio real de e-mail e primeiro acesso não foram executados.

## Hospedagem

O proxy do Angular funciona apenas no servidor de desenvolvimento. Em produção, configure o servidor web para encaminhar os mesmos caminhos de proxy.conf.json ao backend e devolver index.html para as rotas do frontend. Não publique o .env nem chaves do Supabase no frontend.
