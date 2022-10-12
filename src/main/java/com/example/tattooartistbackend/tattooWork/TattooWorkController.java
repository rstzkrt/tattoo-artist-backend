package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.generated.apis.TattooWorksApi;
import com.example.tattooartistbackend.generated.models.*;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TattooWorkController implements TattooWorksApi {

    private final TattooWorkService tattooWorkService;
    private final TattooWorkEsService tattooWorkEsService;

    /**
     * POST /tattoo-works
     * create
     *
     * @param tattooWorkPostRequestDto (required)
     * @return Created (status code 201)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorksResponseDto> createTattooWork(TattooWorkPostRequestDto tattooWorkPostRequestDto) {
        return new ResponseEntity<>(tattooWorkService.createTattooWork(tattooWorkPostRequestDto), HttpStatus.CREATED);
    }

    /**
     * DELETE /tattoo-works/{id}
     * delete
     *
     * @param id tattoo-work id (required)
     * @return no content (status code 204)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<Void> deleteTattooWork(UUID id) {
        tattooWorkService.deleteTattooWork(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * GET /tattoo-works
     * get all tattooWorks
     *
     * @param price   price (required)
     * @param page    (required)
     * @param size    (required)
     * @param country country (optional)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorkResponsePageable> getAllTattooWorks(BigDecimal price, Integer page, Integer size, String country) {
        return ResponseEntity.ok(tattooWorkService.getAllTattooWorks(page, size, price, country));
    }

    /**
     * GET /tattoo-works/{id}
     * get tattoo work
     *
     * @param id tattoo-work id (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorksResponseDto> getTattooWorkById(UUID id) {
        return ResponseEntity.ok(tattooWorkService.getTattooWorkById(id));
    }

    /**
     * PATCH /tattoo-works/{id}
     * patch
     *
     * @param id                        tattoo-work id (required)
     * @param tattooWorkPatchRequestDto (required)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorksResponseDto> patchTattooWork(UUID id, TattooWorkPatchRequestDto tattooWorkPatchRequestDto) {
        return new ResponseEntity<>(tattooWorkService.patchTattooWork(id, tattooWorkPatchRequestDto), HttpStatus.CREATED);
    }

    /**
     * GET /tattoo-works/search
     * search
     *
     * @param page        (required)
     * @param size        (required)
     * @param query       description (optional)
     * @param minPrice    query keyword (optional)
     * @param maxPrice    query keyword (optional)
     * @param currency    query keyword (optional)
     * @param tattooStyle query tattooStyle (optional)
     * @return OK (status code 200)
     * or error payload (status code 200)
     */
    @Override
    public ResponseEntity<TattooWorkResponsePageable> searchTattooWorks(Integer page, Integer size, String query, Integer minPrice, Integer maxPrice, String currency, String tattooStyle) {
        return new ResponseEntity<>(tattooWorkEsService.getTattooWorkSearchResults(query, minPrice, maxPrice, currency, tattooStyle,page ,size), HttpStatus.OK);
    }
}
