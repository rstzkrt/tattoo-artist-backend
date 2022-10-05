package com.example.tattooartistbackend.user.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserEsRepository extends ElasticsearchRepository<UserDocument, UUID> {
    @Query("{\"bool\": {\"must\": {\"multi_match\": {\"query\": \"?0\",\"fields\": [\"firstName\",\"lastName\"], \"fuzziness\": 2}},\"filter\": [{\"term\": {\"city\": \"?1\"},\"term\": {\"country\": \"?2\"}}]}}")
    Page<UserDocument> findByName(String query, String city, String country, Pageable pageable);

}