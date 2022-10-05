package com.example.tattooartistbackend.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByPostedBy_Id(UUID postedById);
    List<Review> findAllByReceiver_Id(UUID receiverId);
}
