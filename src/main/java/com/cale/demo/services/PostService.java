package com.cale.demo.services;

import com.cale.demo.models.PostModel;
import com.cale.demo.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;


    public ArrayList<PostModel> obtenerPosts() {
        return (ArrayList<PostModel>) postRepository.findAll();
    }

    public PostModel guardarPost(PostModel postModel) {
        return postRepository.save(postModel);
    }

    public Optional<PostModel> obtenerPostPorID(Long id) {
        return postRepository.findById(id);
    }

    public boolean eliminarPost(Long id) {
        try {
            postRepository.deleteById(id);
            return true; // TODO revisar
        }catch (Exception e) {
            return false;
        }

    }
}
