package com.example.tattooartistbackend.tattooWork;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Repository
public interface TattooWorkRepository extends JpaRepository<TattooWork, UUID> {

    //user price range or price interval
    TattooWork findTopByMadeBy_IdOrderByPriceDesc(UUID madeBy);

    TattooWork findTopByMadeBy_IdOrderByPriceAsc(UUID madeBy);

    @Query("SELECT t from TattooWork t WHERE (:country is null or t.madeBy.businessAddress.country = :country) AND (:price is null or t.price = :price)")
    List<TattooWork> findAllFilter(String country, BigDecimal price);
}
