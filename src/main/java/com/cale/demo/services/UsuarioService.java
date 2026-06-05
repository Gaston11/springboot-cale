package com.cale.demo.services;

import com.cale.demo.exepciones.RecursoNoEncontradoException;
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    public ArrayList<UsuarioModel> obtenerUsuariosPorPrioridad(Integer prioridad) {
        return Optional.of((ArrayList<UsuarioModel>) usuarioRepository.findByPrioridad(prioridad))
                .filter(ArrayList -> !ArrayList.isEmpty())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario/s no encontrado con prioridad: " + prioridad));
    }

    public void eliminarUsuario(Long id) {
        if (this.obtenerPorId(id) != null) {
            usuarioRepository.deleteById(id);
        }
    }
}
