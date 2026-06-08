package com.cale.demo.repositories;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.models.PostModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostModel, Long> {

    Page<PostModel> findByTituloContainingIgnoreCase(Pageable pageable, String titulo);

    Page<PostModel> findByUsuarioId(Pageable pageable, Long id);

}
