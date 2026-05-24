package com.cale.demo.services;

import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public CurrentUserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioModel getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return this.usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );
    }
}
