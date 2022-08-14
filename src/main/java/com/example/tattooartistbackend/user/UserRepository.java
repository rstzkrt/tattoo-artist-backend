package com.example.tattooartistbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u from User u WHERE (:firstName is null or u.firstName = :firstName)")
    List<User> findAllUsers(String firstName);

}
