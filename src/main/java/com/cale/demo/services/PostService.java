package com.cale.demo.services;

import com.cale.demo.dtos.*;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.OperacionInvalidaException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.*;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.ComentarioRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private final PostRepository postRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;
    private final ComentarioRepository comentarioRepository;

    public PostService(UsuarioRepository usuarioRepository, PostRepository postRepository, CategoriaRepository categoriaRepository, CurrentUserService currentUserService, ComentarioRepository comentarioRepository) {
        this.postRepository = postRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
        this.comentarioRepository = comentarioRepository;
    }

    public PageResponse<PostResponseDto> obtenerPosts(Pageable pageable, String titulo, Long usuarioId) {
        Page<PostModel> pagina = postRepository.findAll(pageable);

        if(titulo != null && !titulo.isBlank()){
            pagina = postRepository.findByTituloContainingIgnoreCase(pageable,titulo);
        }

        if(usuarioId != null){
            pagina = postRepository.findByUsuarioId(pageable,usuarioId);
        }

        return new PageResponse<>(
                pagina.getContent().stream().map(this::convertirADto).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }

    public PostResponseDto guardarPost(PostRequestDto postRequestDto) {
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();

        PostModel postModel = new PostModel();
        postModel.setTitulo(postRequestDto.getTitulo());
        postModel.setDescripcion(postRequestDto.getDescripcion());

        postModel.setUsuario(usuarioModel);

        Set<Long> categoriaModelSet = postRequestDto.getCategoriaIds();
        if (categoriaModelSet.isEmpty()) {
            throw new OperacionInvalidaException("El post debe tener al menos una categoria");
        }

        Set<CategoriaModel> categorias = new HashSet<>((Collection) categoriaRepository.findAllById(categoriaModelSet));
        if (categorias.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encuentran esas categorias");
        }

        postModel.setCategorias(categorias);

        return convertirADto(postRepository.save(postModel));
    }

    private PostResponseDto convertirADto(PostModel postModel) {
        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto();
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(postModel.getId());
        postResponseDto.setTitulo(postModel.getTitulo());
        postResponseDto.setDescripcion(postModel.getDescripcion());
        usuarioResponseDto.setNombre(postModel.getUsuario().getNombre());
        usuarioResponseDto.setId(postModel.getUsuario().getId());
        postResponseDto.setUsuario(usuarioResponseDto);
        postResponseDto.setFechaCreacion(postModel.getFechaCreacion());
        postResponseDto.setFechaActualizacion(postModel.getFechaModificacion());      postResponseDto.setNombreCategorias(postModel.getCategorias()
        .stream().map(c -> c.getNombre()).collect(Collectors.toSet()));

        return postResponseDto;
    }

    public PostResponseDto obtenerPostPorID(Long id) {
        return convertirADto(obtenerPostModelPorID(id));
    }

    private PostModel obtenerPostModelPorID(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Post no encontrado con ID: " + id));
    }

    public void eliminarPost(Long id) {
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();
        PostModel postActual = obtenerPostModelPorID(id);

        if ((!postActual.getUsuario().getId().equals(usuarioModel.getId()))
                && (usuarioModel.getRol() != Rol.ADMIN) ) {
            throw new NoAutorizadoException("No puedes editar este post");
        }

        postRepository.deleteById(id);
    }

    public PostResponseDto actualizarPost(@Valid PostRequestDto postRequestDto, Long id) {
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();

        PostModel postActual = obtenerPostModelPorID(id);

        if ((!postActual.getUsuario().getId().equals(usuarioModel.getId()))
                && (usuarioModel.getRol() != Rol.ADMIN) ) {
            throw new NoAutorizadoException("No puedes editar este post");
        }

        postActual.setTitulo(postRequestDto.getTitulo());
        postActual.setDescripcion(postRequestDto.getDescripcion());
        Set<CategoriaModel> categoriaModels = postRequestDto.getCategoriaIds().stream().
                map(idCategoria -> categoriaRepository.findById(idCategoria).
                        orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada: " + idCategoria))).
                collect(Collectors.toSet());
        postActual.setCategorias(categoriaModels);

        return convertirADto(postRepository.save(postActual));
    }

    public ComentarioResponseDto guardarComentario(Long id, ComentarioRequestDto comentarioRequestDto) {
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();
        PostModel postActual = obtenerPostModelPorID(id);
        ComentarioModel comentarioModel = new ComentarioModel();
        ComentarioResponseDto comentarioResponseDto = new ComentarioResponseDto();

        if (postActual != null){
            comentarioModel.setPost(postActual);
            comentarioModel.setComentario(comentarioRequestDto.getComentario());
            comentarioModel.setUsuario(usuarioModel);
            this.comentarioRepository.save(comentarioModel);
            comentarioResponseDto.setComentario(comentarioModel.getComentario());
            comentarioResponseDto.setId(comentarioModel.getId());
            comentarioResponseDto.setFechaCreacion(comentarioModel.getFechaCreacion());
            comentarioResponseDto.setFechaActualizacion(comentarioModel.getFechaModificacion());
        }
        return comentarioResponseDto;
    }

    public List<ComentarioResponseDto> obtenerComentarios(Long id) {
        List<ComentarioResponseDto> comentarioResponseDtos = new ArrayList<>();
        if (obtenerPostModelPorID(id) != null){
            Set<ComentarioModel> comentarioModelSet = new HashSet<>();
            comentarioModelSet = this.comentarioRepository.findByPostId(id);
            for (ComentarioModel comentarioModel : comentarioModelSet) {
                ComentarioResponseDto comentarioResponseDto = new ComentarioResponseDto();
                comentarioResponseDto.setComentario(comentarioModel.getComentario());
                comentarioResponseDto.setId(comentarioModel.getId());
                comentarioResponseDtos.add(comentarioResponseDto);
            }
        }
        return comentarioResponseDtos;
    }
}
