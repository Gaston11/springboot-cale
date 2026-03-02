package com.cale.demo.services;

import com.cale.demo.exepciones.RecursoNoEncontradoExepcion;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired //para no crear la instancia nueva
    UsuarioRepository usuarioRepository;

    public ArrayList<UsuarioModel> obtenerUsuarios() {
        return (ArrayList<UsuarioModel>) usuarioRepository.findAll();
    }

    public UsuarioModel guardarUsuario(UsuarioModel usuarioModel) {
        return usuarioRepository.save(usuarioModel);
    }

    public UsuarioModel obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExepcion("Usuario no encontrado con ID: " + id));
    }

    public ArrayList<UsuarioModel> obtenerUsuariosPorPrioridad(Integer prioridad) {
        return (ArrayList<UsuarioModel>) usuarioRepository.findByPrioridad(prioridad);
    }

    public boolean eliminarUsuario(Long id) {
        try{
            usuarioRepository.deleteById(id);
            return true; // TODO revisar
        }catch(Exception e){
            return false;
        }
    }
}
