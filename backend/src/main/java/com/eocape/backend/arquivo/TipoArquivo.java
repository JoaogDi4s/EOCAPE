package com.eocape.backend.arquivo;

public enum TipoArquivo {
    ATA("ata"),
    CHAMADO("chamado"),
    CLASSIFICADO("classificado"),
    MANUTENCAO("manutencao");

    private final String pasta;

    TipoArquivo(String pasta) {
        this.pasta = pasta;
    }

    public String pasta() {
        return pasta;
    }
}