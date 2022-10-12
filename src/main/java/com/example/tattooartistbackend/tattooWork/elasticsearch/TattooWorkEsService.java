package com.example.tattooartistbackend.tattooWork.elasticsearch;

import com.example.tattooartistbackend.exceptions.TattooWorkNotFoundException;
import com.example.tattooartistbackend.generated.models.TattooWorkResponsePageable;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.tattooWork.TattooWorkService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TattooWorkEsService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final TattooWorkRepository tattooWorkRepository;
    private BoolQueryBuilder createBoolQueryForTattooWork(String query, Integer minPrice, Integer maxPrice, String currency, String tattooStyle) {
        var boolQuery = new BoolQueryBuilder();
        if(!query.equals("")){
            var matchPhrasePrefixQuery1 = new MatchPhrasePrefixQueryBuilder("description", query);
            boolQuery.must(matchPhrasePrefixQuery1);
        }
        if (ObjectUtils.isNotEmpty(currency)) {
            var currencyFilterQuery = new TermQueryBuilder("currency", currency);
            boolQuery.filter(currencyFilterQuery);
        }
        if (ObjectUtils.isNotEmpty(minPrice)) {
            var minPriceRangeQueryBuilder  = new RangeQueryBuilder("price").gte(minPrice);
            boolQuery.filter(minPriceRangeQueryBuilder);
        }
        if (ObjectUtils.isNotEmpty(maxPrice)) {
            var maxPriceRangeQueryBuilder  = new RangeQueryBuilder("price").lte(maxPrice);
            boolQuery.filter(maxPriceRangeQueryBuilder);
        }
        if (ObjectUtils.isNotEmpty(tattooStyle)) {
            var tattooStyleFilterQuery = new TermQueryBuilder("tattooStyle", tattooStyle.toLowerCase());
            boolQuery.filter(tattooStyleFilterQuery);
        }
        return boolQuery;
    }

    public TattooWorkResponsePageable getTattooWorkSearchResults(String query, Integer minPrice, Integer maxPrice, String currency, String tattooStyle, Integer page, Integer size) {
        var request = new Request("GET", "/tattoo/_search");
        var searchSource = new SearchSourceBuilder();
        searchSource.from(page).size(size);
        searchSource.query(createBoolQueryForTattooWork(query, minPrice,maxPrice,currency, tattooStyle));
        request.setJsonEntity(searchSource.toString());
        final JsonNode jsonNode;
        try {
            var response = client.getLowLevelClient().performRequest(request);
            var responseBody = EntityUtils.toString(response.getEntity());
            jsonNode = objectMapper.readTree(responseBody).get("hits").get("hits");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        List<TattooWorksResponseDto> tattooWorksResponseList = new ArrayList<>();
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        Iterator<JsonNode> itr = arrayNode.elements();
        while (itr.hasNext()) {
            JsonNode jsonNode1 = itr.next().get("_source");
            var tattooWorkId = jsonNode1.get("id").asText();
            tattooWorksResponseList.add(TattooWork.toTattooWorksResponseDto(tattooWorkRepository.findById(UUID.fromString(tattooWorkId)).orElseThrow(TattooWorkNotFoundException::new)));
        }
        TattooWorkResponsePageable tattooWorkResponsePageable = new TattooWorkResponsePageable();
        tattooWorkResponsePageable.setTattooWorks(tattooWorksResponseList);
        tattooWorkResponsePageable.setTotalElements(tattooWorksResponseList.size());
        return tattooWorkResponsePageable;
    }

}
