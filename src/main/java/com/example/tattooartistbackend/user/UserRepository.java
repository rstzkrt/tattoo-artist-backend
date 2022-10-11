package com.example.tattooartistbackend.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u from User u WHERE (:firstName is null or u.firstName = :firstName) " +
            "AND (:lastName is null or u.lastName = :lastName)")
    List<User> findAllUsers(String firstName,String lastName);
    List<User> findAllByFavouriteArtistsIn(List<User> users);
    Optional<User> findByUid(String uid);
    @Query("SELECT u from User u WHERE u.hasArtistPage=true AND (:firstName is null or u.firstName = :firstName) " +
            "AND (:lastName is null or u.lastName = :lastName)")
    Page<User> findAllTattooArtist(String firstName, String lastName, Pageable pageable);
}
