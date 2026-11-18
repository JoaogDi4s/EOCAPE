<##
Gera a referência JavaDoc do backend, incluindo membros privados.
O delombok expande os acessores gerados antes da execução do JavaDoc.
Os avisos de documentação ausente em membros gerados pelo Lombok são
suprimidos; as demais verificações de estrutura e referências permanecem ativas.
##>
$ErrorActionPreference = 'Stop'
$backendPath = Split-Path -Parent $PSScriptRoot
Push-Location -LiteralPath $backendPath
try {
    & .\mvnw.cmd '-DskipTests' compile dependency:build-classpath '-Dmdep.outputFile=target/javadoc-classpath.txt'
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar ou resolver o classpath do backend.' }

    $classpath = (Get-Content -LiteralPath 'target/javadoc-classpath.txt' -Raw).Trim()
    $lombokJar = $classpath.Split([IO.Path]::PathSeparator) |
        Where-Object { [IO.Path]::GetFileName($_) -match '^lombok-.*\.jar$' } |
        Select-Object -First 1
    if (-not $lombokJar) { throw 'O classpath não contém o Lombok.' }

    $encoding = [Text.UTF8Encoding]::new($false)
    function Write-JavaArguments($file, $values) {
        $lines = $values | ForEach-Object { '"' + $_.Replace('\', '/').Replace('"', '\"') + '"' }
        [IO.File]::WriteAllLines((Join-Path $backendPath $file), [string[]]$lines, $encoding)
    }

    Write-JavaArguments 'target/delombok.args' @(
        '-jar', $lombokJar, 'delombok', 'src/main/java',
        '-d', 'target/javadoc-sources', '--classpath', $classpath, '--encoding', 'UTF-8'
    )
    & java '@target/delombok.args'
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao expandir o código gerado pelo Lombok.' }

    $sources = Get-ChildItem -LiteralPath 'target/javadoc-sources' -Recurse -Filter '*.java' |
        Select-Object -ExpandProperty FullName
    Write-JavaArguments 'target/javadoc.args' (@(
        '-private', '-author', '-version', '-quiet', '-notimestamp',
        '-Xdoclint:all,-missing', '-encoding', 'UTF-8',
        '-docencoding', 'UTF-8', '-charset', 'UTF-8',
        '-d', 'target/site/apidocs', '-classpath', $classpath
    ) + $sources)
    & javadoc '@target/javadoc.args'
    if ($LASTEXITCODE -ne 0) { throw 'O JavaDoc encontrou erros na documentação.' }
    Write-Output (Join-Path $backendPath 'target/site/apidocs/index.html')
}
finally {
    Pop-Location
}
