package com.cale.demo.controllers;

import com.cale.demo.dtos.PostRequestDto;
import com.cale.demo.dtos.PostResponseDto;
import com.cale.demo.models.PostModel;
import com.cale.demo.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ArrayList<PostModel> obtenerPosts() {
        return postService.obtenerPosts();
    }

    @PostMapping
    public PostResponseDto guardarPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        return postService.guardarPost(postRequestDto);
    }

    @GetMapping(path = "/{id}")
    public PostModel obtenerPostPorID(@PathVariable("id") Long id){
        return postService.obtenerPostPorID(id);
    }

    @DeleteMapping(path = "/{id}")
    public void eliminarPost(@PathVariable("id") Long id){
        postService.eliminarPost(id);
    }
}
