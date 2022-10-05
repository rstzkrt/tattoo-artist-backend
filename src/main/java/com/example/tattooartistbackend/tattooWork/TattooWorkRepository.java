package com.example.tattooartistbackend.tattooWork;


import com.example.tattooartistbackend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface TattooWorkRepository extends JpaRepository<TattooWork, UUID> {

    Optional<TattooWork> findTopByMadeBy_IdOrderByConvertedPriceValueDesc(UUID madeBy);

    Optional<TattooWork> findTopByMadeBy_IdOrderByConvertedPriceValueAsc(UUID madeBy);

    @Query("SELECT t from TattooWork t WHERE (:country is null or t.madeBy.businessAddress.country = :country) " +
            "AND (:price is null or t.price = :price)")
    List<TattooWork> findAllFilter(String country, BigDecimal price);

    List<TattooWork> findAllByClient_Id(UUID id);

    @Query("SELECT t from TattooWork t where t.price > ?1")
    Page<TattooWork> findAllByPriceGreaterThan(BigDecimal price,Pageable pageable);

    @Query("SELECT t from TattooWork t where t.madeBy.id = ?1")
    List<TattooWork> findAllByMadeBy_Id(UUID id);

    boolean existsByLikerIdsContainsAndId(User user,UUID tattooWorkId);
    boolean existsByDislikerIdsContainsAndId(User user,UUID tattooWorkId);

    List<TattooWork> findByLikerIdsIn(List<User> likerIds);
    List<TattooWork> findByDislikerIdsIn(List<User> dislikerIds);
}
