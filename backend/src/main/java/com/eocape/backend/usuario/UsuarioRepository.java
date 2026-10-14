package com.eocape.backend.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByCpfAndAtivoTrue(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}