export type Perfil = "MORADOR" | "SINDICO" | "ADMINISTRADORA" | "OUTRO";

export interface Lead {
    id: string;
    nome: string;
    email: string;
    telefone: string | null;
    nome_condominio: string | null;
    perfil: Perfil;
    consentimento_contato: boolean;
}