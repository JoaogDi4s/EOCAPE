package com.eocape.backend.arquivo;

/**
 * Define os valores admitidos para tipo arquivo no domínio do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public enum TipoArquivo {
    /**
     * Estado ou opção ata admitido por tipo arquivo.
     */
    ATA("ata"),
    /**
     * Estado ou opção chamado admitido por tipo arquivo.
     */
    CHAMADO("chamado"),
    /**
     * Estado ou opção classificado admitido por tipo arquivo.
     */
    CLASSIFICADO("classificado"),
    /**
     * Estado ou opção manutencao admitido por tipo arquivo.
     */
    MANUTENCAO("manutencao");

    /**
     * Segmento de pasta utilizado na chave de armazenamento do tipo de arquivo.
     */
    private final String pasta;

    /**
     * Inicializa TipoArquivo com os dados e as dependências necessários à operação.
     *
     * @param pasta Segmento de pasta utilizado na chave de armazenamento do tipo de arquivo.
     */
    TipoArquivo(String pasta) {
        this.pasta = pasta;
    }

    /**
     * Obtém o segmento da pasta correspondente ao tipo de arquivo.
     *
     * @return valor textual produzido pela operação.
     */
    public String pasta() {
        return pasta;
    }
}