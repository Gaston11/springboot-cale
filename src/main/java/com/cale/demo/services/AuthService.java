package com.cale.demo.services;

import com.cale.demo.dtos.LoginRequest;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.exepciones.RecursoNoEncontradoExepcion;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import com.cale.demo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public UsuarioModel register(RegisterRequest registerRequest) {
        UsuarioModel usuarioModel = new UsuarioModel();

        usuarioModel.setNombre(registerRequest.getNombre());
        usuarioModel.setApellido(registerRequest.getApellido());
        usuarioModel.setEmail(registerRequest.getEmail());
        usuarioModel.setPrioridad(registerRequest.getPrioridad());

        String passwordHash = this.passwordEncoder.encode(registerRequest.getPassword());
        usuarioModel.setPassword(passwordHash);

        usuarioModel.setRol(Rol.USER);

        return usuarioRepository.save(usuarioModel);
    }

    public String login(LoginRequest loginRequest){
        UsuarioModel usuarioModel = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RecursoNoEncontradoExepcion("Usuario no encontrado"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), usuarioModel.getPassword())){
            throw new RuntimeException("Contraseña incorrecta");
        }

        return jwtService.generarToken(usuarioModel.getEmail());
    }

}
