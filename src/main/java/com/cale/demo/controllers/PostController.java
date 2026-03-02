package com.cale.demo.controllers;

import com.cale.demo.models.PostModel;
import com.cale.demo.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

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
    public PostModel guardarPost(@RequestBody PostModel postModel){
        return postService.guardarPost(postModel);
    }

    @GetMapping(path = "/{id}")
    public Optional<PostModel> obtenerPostPorID(@PathVariable("id") Long id){
        return postService.obtenerPostPorID(id);
    }

    @DeleteMapping(path = "/{id}")
    public String eliminarPost(@PathVariable("id") Long id){
        boolean ok = postService.eliminarPost(id);
        if(ok){
            return "Post eliminado con id: " + id;
        }else {
            return "Post no encontrado con id: " + id;
        }
    }
}
