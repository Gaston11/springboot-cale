package com.cale.demo.config;

import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.ComentarioModel;
import com.cale.demo.models.PostModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.ComentarioRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            PostRepository postRepository,
            ComentarioRepository comentarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Usuario demo
            UsuarioModel usuario = usuarioRepository
                    .findByEmail("demo@cale.com")
                    .orElseGet(() -> {
                        UsuarioModel nuevoUsuario = new UsuarioModel();

                        nuevoUsuario.setNombre("Usuario");
                        nuevoUsuario.setApellido("Demo");
                        nuevoUsuario.setEmail("demo@cale.com");
                        nuevoUsuario.setPassword(passwordEncoder.encode("demo1234"));
                        nuevoUsuario.setPrioridad(10);
                        nuevoUsuario.setRol(Rol.USER);

                        return usuarioRepository.save(nuevoUsuario);
                    });

            // Categoría demo
            CategoriaModel categoria = categoriaRepository.findAll()
                    .stream()
                    .filter(c -> c.getNombre().equals("Tecnología"))
                    .findFirst()
                    .orElseGet(() -> {
                        CategoriaModel nuevaCategoria = new CategoriaModel();
                        nuevaCategoria.setNombre("Tecnología");

                        return categoriaRepository.save(nuevaCategoria);
                    });

            // Post demo
            PostModel post = postRepository.findAll()
                    .stream()
                    .filter(p -> p.getTitulo().equals("Mi primer post"))
                    .findFirst()
                    .orElseGet(() -> {
                        PostModel nuevoPost = new PostModel();

                        nuevoPost.setTitulo("Mi primer post");
                        nuevoPost.setDescripcion(
                                "Este es un post de demostración creado automáticamente por Docker."
                        );
                        nuevoPost.setUsuario(usuario);

                        HashSet<CategoriaModel> categorias = new HashSet<>();
                        categorias.add(categoria);
                        nuevoPost.setCategorias(categorias);

                        return postRepository.save(nuevoPost);
                    });

            // Comentario demo
            boolean comentarioExiste = comentarioRepository.findByPostId(post.getId())
                    .stream()
                    .anyMatch(c ->
                            c.getComentario().equals(
                                    "Este es un comentario de demostración."
                            )
                    );

            if (!comentarioExiste) {
                ComentarioModel comentario = new ComentarioModel();

                comentario.setComentario(
                        "Este es un comentario de demostración."
                );
                comentario.setUsuario(usuario);
                comentario.setPost(post);

                comentarioRepository.save(comentario);
            }
        };
    }
}
