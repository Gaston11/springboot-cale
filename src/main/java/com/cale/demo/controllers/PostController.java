package com.cale.demo.controllers;

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
    public PostModel guardarPost(@Valid @RequestBody PostModel postModel){
        return postService.guardarPost(postModel);
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
