package com.cale.demo.repositories;

import com.cale.demo.models.ComentarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioModel, Long> {

    Set<ComentarioModel> findByPostId(Long id);
}
