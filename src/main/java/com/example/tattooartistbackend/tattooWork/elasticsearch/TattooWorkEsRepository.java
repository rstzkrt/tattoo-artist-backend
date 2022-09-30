package com.example.tattooartistbackend.tattooWork.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TattooWorkEsRepository extends ElasticsearchRepository<TattooWorkDocument, String> {

}