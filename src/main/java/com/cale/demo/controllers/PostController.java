package com.cale.demo.controllers;

import com.cale.demo.dtos.PostRequestDto;
import com.cale.demo.dtos.PostResponseDto;
import com.cale.demo.models.PostModel;
import com.cale.demo.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ArrayList<PostResponseDto> obtenerPosts() {
        return postService.obtenerPosts();
    }

    @PostMapping
    public PostResponseDto guardarPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return postService.guardarPost(postRequestDto,email);
    }

    @GetMapping(path = "/{id}")
    public PostResponseDto obtenerPostPorID(@PathVariable("id") Long id){
        return postService.obtenerPostPorID(id);
    }

    @DeleteMapping(path = "/{id}")
    public void eliminarPost(@PathVariable("id") Long id){
        postService.eliminarPost(id);
    }

    @PutMapping(path = "/{id}")
    public PostResponseDto actualizarPost( @PathVariable("id") Long id, @Valid @RequestBody PostRequestDto postRequestDto) {
        return postService.actualizarPost(postRequestDto,id);
    }
}
