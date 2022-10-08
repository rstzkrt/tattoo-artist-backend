package com.example.tattooartistbackend.user.elasticsearch;

import com.example.tattooartistbackend.generated.models.Gender;
import com.example.tattooartistbackend.generated.models.UserDocumentDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEsService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    private BoolQueryBuilder createBoolQueryForUser(String query, String city, String country, Boolean isTattooArtist, Double averageRating, List<String> languages, Gender gender) {
        var boolQuery = new BoolQueryBuilder();
        if (!query.equals("")) {
            var matchPhrasePrefixQuery1 = new MatchPhrasePrefixQueryBuilder("fullName", query);
            boolQuery.must(matchPhrasePrefixQuery1);
        }
        if (ObjectUtils.isNotEmpty(city)) {
            var cityFilterQuery = new TermQueryBuilder("city", city.toLowerCase());
            boolQuery.filter(cityFilterQuery);
        }
        if (ObjectUtils.isNotEmpty(country)) {
            var countryFilterQuery = new TermQueryBuilder("country", country.toLowerCase());
            boolQuery.filter(countryFilterQuery);
        }
        if (ObjectUtils.isNotEmpty(isTattooArtist)) {
            var isTattooArtistTermQuery = new TermQueryBuilder("hasTattooArtistAcc", isTattooArtist);
            boolQuery.filter(isTattooArtistTermQuery);
        }
        if (ObjectUtils.isNotEmpty(averageRating)) {
            var rangeQueryBuilder = new RangeQueryBuilder("averageRating").gte(averageRating);
            boolQuery.filter(rangeQueryBuilder);
        }
        if (ObjectUtils.isNotEmpty(languages)) {
            var languagesTermQuery = new TermsQueryBuilder("languages", languages.stream().map(String::toLowerCase).toList());
            boolQuery.filter(languagesTermQuery);
        }
        System.out.println(boolQuery);
        return boolQuery;
    }

    public List<UserDocumentDto> getUserSearchResults(String query, String city, String country, Boolean isTattooArtist, Double averageRating, List<String> languages, Gender gender) {
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
        });
        var request = new Request("GET", "/user/_search");
        var searchSource = new SearchSourceBuilder();
        searchSource.size(20);
        searchSource.query(createBoolQueryForUser(query, city, country, isTattooArtist, averageRating, languages, gender));
        request.setJsonEntity(searchSource.toString());
        JsonNode jsonNode;
        try {
            var response = client.getLowLevelClient().performRequest(request);
            var responseBody = EntityUtils.toString(response.getEntity());
            jsonNode = objectMapper.readTree(responseBody).get("hits").get("hits");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        List<UserDocumentDto> userDocumentList = new ArrayList<>();
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        Iterator<JsonNode> itr = arrayNode.elements();
        while (itr.hasNext()) {
            JsonNode jsonNode1 = itr.next().get("_source");
            var id = jsonNode1.get("id").asText();
            var fullName = jsonNode1.get("fullName") == null ? "empty" : jsonNode1.get("fullName").asText();
            var languages1 = (ArrayNode) jsonNode1.get("languages");
            var gender1 = jsonNode1.get("gender")==null ? "":jsonNode1.get("gender").asText();
            List<String> list = null;
            try {
                if (languages1 != null) {
                    list = reader.readValue(languages1);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Boolean hasTattooArtistAcc = null;
            if (jsonNode1.get("hasTattooArtistAcc") != null) {
                hasTattooArtistAcc = jsonNode1.get("hasTattooArtistAcc").asBoolean();
            }
            var avatarUrl = jsonNode1.get("avatarUrl") == null ? "empty" : jsonNode1.get("avatarUrl").asText();
            var userDocument = UserDocument.builder()
                    .id(UUID.fromString(id))
                    .fullName(fullName)
                    .hasTattooArtistAcc(Boolean.TRUE.equals(hasTattooArtistAcc))
                    .avatarUrl(avatarUrl)
                    .languages(list == null ? new ArrayList<>() : list)
                    .build();
            if(!gender1.equals("")){
                userDocument.setGender(Gender.valueOf(gender1));
            }
            userDocumentList.add(UserDocument.toDto(userDocument));
        }
        return userDocumentList;
    }
}
