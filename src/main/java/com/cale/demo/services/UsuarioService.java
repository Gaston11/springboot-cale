package com.cale.demo.services;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.dtos.UsuarioResponseDto;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;



@Service
public class UsuarioService {
    @Autowired //para no crear la instancia nueva
    UsuarioRepository usuarioRepository;

    public PageResponse<UsuarioResponseDto> obtenerUsuarios(Pageable pageable) {
        Page<UsuarioModel> pagina = usuarioRepository.findAll(pageable);

        return new PageResponse<>(
                pagina.getContent().stream().map(this::convertirAUsuarioDto).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }

    private UsuarioResponseDto convertirAUsuarioDto(UsuarioModel usuarioModel) {
        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setId(usuarioModel.getId());
        usuarioResponseDto.setNombre(usuarioModel.getNombre());
        usuarioResponseDto.setFechaCreacion(usuarioModel.getFechaCreacion());
        usuarioResponseDto.setFechaActualizacion(usuarioModel.getFechaCreacion());

        return usuarioResponseDto;
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
