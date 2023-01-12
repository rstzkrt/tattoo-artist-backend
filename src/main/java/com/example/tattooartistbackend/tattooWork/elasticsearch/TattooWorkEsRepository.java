package com.example.tattooartistbackend.tattooWork.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TattooWorkEsRepository extends ElasticsearchRepository<TattooWorkDocument, UUID> {

}