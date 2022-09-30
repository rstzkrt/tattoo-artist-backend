package com.example.tattooartistbackend.tattooWorkReport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TattooWorkReportRepository extends JpaRepository<TattooWorkReport, UUID> {

    @Query("select t from TattooWorkReport t")
    Page<TattooWorkReport> getAllPageable(Pageable pageable);
}
