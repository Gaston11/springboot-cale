package com.cale.demo.services;

import com.cale.demo.dtos.ComentarioRequestDto;
import com.cale.demo.dtos.ComentarioResponseDto;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.ComentarioModel;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComentarioService {


    @Autowired
    private final ComentarioRepository comentarioRepository;
    private final CurrentUserService currentUserService;

    public ComentarioService(ComentarioRepository comentarioRepository, CurrentUserService currentUserService) {
        this.comentarioRepository = comentarioRepository;
        this.currentUserService = currentUserService;
    }

    public ComentarioResponseDto editarComentario(long idComentario, ComentarioRequestDto  comentarioRequestDto) {
        ComentarioModel comentarioModel = new ComentarioModel();
        comentarioModel = obtenerComentarioModelPorID(idComentario);
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();

        if (comentarioModel.getUsuario().getId() != usuarioModel.getId()) {
            throw new NoAutorizadoException("El usuario no tiene permiso para editar este comentario");
        }

        comentarioModel.setComentario(comentarioRequestDto.getComentario());
        comentarioRepository.save(comentarioModel);
        ComentarioResponseDto comentarioResponseDto = new ComentarioResponseDto();
        comentarioResponseDto.setComentario(comentarioModel.getComentario());
        comentarioResponseDto.setId(comentarioModel.getId());
        return comentarioResponseDto;
    }

    public void eliminarComentario(long id) {
       if (this.obtenerComentarioModelPorID(id) != null) {
           comentarioRepository.deleteById(id);
       }
    }

    private ComentarioModel obtenerComentarioModelPorID(Long id) {
        return this.comentarioRepository.findById(id).
                orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));
    }
}
