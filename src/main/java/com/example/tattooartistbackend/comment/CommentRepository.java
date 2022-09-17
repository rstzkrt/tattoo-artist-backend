package com.example.tattooartistbackend.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Comment findByTattooWork_Id(UUID tattooWorkId);

    boolean existsByPostedBy_Id(UUID uuid);
}
