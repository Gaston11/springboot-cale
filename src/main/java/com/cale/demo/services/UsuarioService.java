package com.cale.demo.services;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.dtos.PrioridadRequest;
import com.cale.demo.dtos.UsuarioResponseDto;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;

    public UsuarioService(CurrentUserService currentUserService, UsuarioRepository usuarioRepository) {
        this.currentUserService = currentUserService;
        this.usuarioRepository = usuarioRepository;
    }


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
        usuarioResponseDto.setFechaActualizacion(usuarioModel.getFechaModificacion());

        return usuarioResponseDto;
    }

    public UsuarioResponseDto obtenerPorId(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return convertirAUsuarioDto(usuarioModel);
    }

    public List<UsuarioResponseDto> obtenerUsuariosPorPrioridad(Integer prioridad) {
        List<UsuarioModel> usuarios = usuarioRepository.findByPrioridad(prioridad);

        if (usuarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron usuarios con esa prioridad");
        }

        return usuarios.stream()
                .map(this::convertirAUsuarioDto)
                .toList();
    }

    public void eliminarUsuario(Long id) {
        UsuarioModel usuarioActual = this.currentUserService.getCurrentUser();

        if (usuarioActual.getRol() != Rol.ADMIN) {
            throw new NoAutorizadoException("No puedes eliminar este usuario");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id);
        }

        usuarioRepository.deleteById(id);

    }

    public UsuarioResponseDto actualizarPrioridad(long id, PrioridadRequest prioridad) {
        UsuarioModel usuarioActual = this.currentUserService.getCurrentUser();

        if (usuarioActual.getRol() != Rol.ADMIN ) {
            throw new NoAutorizadoException("No puedes editar este usuario");
        }

        UsuarioModel usuarioModel = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        usuarioModel.setPrioridad(prioridad.getPrioridad());
        return this.convertirAUsuarioDto(usuarioRepository.save(usuarioModel));

    }
}
