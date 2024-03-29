package com.example.tattooartistbackend.tattooWork;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TattooWorkRepository extends JpaRepository<TattooWork, UUID> {
    TattooWork findTopByMadeBy_IdOrderByPriceDesc(UUID madeBy);

    TattooWork findTopByMadeBy_IdOrderByPriceAsc(UUID madeBy);
}
