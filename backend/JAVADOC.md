# Documentação JavaDoc

Os arquivos Java em `src/main/java` e `src/test/java` documentam classes,
atributos e métodos. Os componentes dos records são descritos por `@param`
na declaração do tipo. Os métodos com resultado utilizam `@return`; as
exceções aplicáveis são descritas com `@throws`.

A autoria coletiva foi identificada como **Equipe EOCAPE**. A versão
`0.0.1-SNAPSHOT` corresponde à versão declarada no `pom.xml`.

Para gerar a referência HTML no PowerShell, na pasta `backend`:

```powershell
.\scripts\gerar-javadoc.ps1
```

É necessário ter o JDK no PATH e acesso às dependências Maven. O comando
compila o projeto, expande os membros gerados pelo Lombok e valida a
estrutura e as referências dos comentários. A referência de produção fica
em `target/site/apidocs/index.html`, incluindo atributos e métodos privados.
Os arquivos gerados em `target` não precisam ser versionados.

## Site HTML padrão do JavaDoc

Abra `target/site/apidocs/index.html` no navegador. A documentação funciona
localmente e inclui busca, navegação entre pacotes e classes, índices e
temas claro e escuro. Para compartilhar ou hospedar, copie a pasta
`target/site/apidocs` inteira: as páginas dependem dos arquivos CSS,
JavaScript e recursos que ficam ao lado do índice.

A documentação utiliza exclusivamente o layout e o CSS padrão do JavaDoc do JDK,
com navegação azul e tabelas em branco e cinza. Não há tema ou página de
apresentação personalizados. O script mantém esse padrão em cada geração.

Não se usa `@return` em construtores ou métodos `void`, nem `@param` em
métodos sem parâmetros. As tags `@author` e `@version` ficam nas declarações
dos tipos. As exceções documentadas fazem parte das validações explícitas e
das chamadas aos serviços do projeto; falhas implícitas de bibliotecas não
são uma lista exaustiva de exceções possíveis.
