package com.example.tattooartistbackend.tattooWork;


import com.example.tattooartistbackend.comment.CommentRepository;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.tattooWork.models.TattooWorkPatchRequestDto;
import com.example.tattooartistbackend.tattooWork.models.TattooWorkPostRequestDto;
import com.example.tattooartistbackend.tattooWork.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.awt.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TattooWorkService {
    private final TattooWorkRepository tattooWorkRepository;
    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public TattooWorksResponseDto createTattooWork(TattooWorkPostRequestDto tattooWorkPostRequestDto){
        var client= userRepository.findById(tattooWorkPostRequestDto.getClientId()).orElseThrow();
        var madeBy=userRepository.findById(tattooWorkPostRequestDto.getMadeById()).orElseThrow();
        if (!client.isHasArtistPage()){
            try {
                throw new Exception("user doesnt have artist page to post tattoo work");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return TattooWork.toTattooWorksResponseDto(tattooWorkRepository.save(TattooWork.fromTattooWorkPostRequest(tattooWorkPostRequestDto,client,madeBy)));
    }

    public void deleteTattooWork(UUID id){
        if (tattooWorkRepository.existsById(id)) {
            tattooWorkRepository.deleteById(id);
        } else {
            throw new NotFoundException("TattooWorkNotFound");
        }
    }
    public List<TattooWorksResponseDto> getAllTattooWorks(String country, BigDecimal price){
       return tattooWorkRepository.findAllFilter(country,price)
               .stream()
               .map(TattooWork::toTattooWorksResponseDto)
               .collect(Collectors.toList());

    }
    public TattooWorksResponseDto patchTattooWork(UUID id, TattooWorkPatchRequestDto tattooWorkPatchRequestDto){
        var tattooWork= tattooWorkRepository.findById(id).orElseThrow();
        tattooWork.setDescription(tattooWorkPatchRequestDto.getDescription());
        tattooWork.setPrice(tattooWorkPatchRequestDto.getPrice());
        tattooWork.setCurrency(tattooWorkPatchRequestDto.getCurrency());
        tattooWork.setPhotos(tattooWorkPatchRequestDto.getPhotos());                //split to 2 patch endpoints add photos - delete photos...
        tattooWork.setCoverPhoto(tattooWorkPatchRequestDto.getCoverPhoto());
        tattooWorkRepository.save(tattooWork);
        return TattooWork.toTattooWorksResponseDto(tattooWork);
    }

    public TattooWorksResponseDto getTattooWorkById(UUID id) {
        var tattooWork=tattooWorkRepository.findById(id).orElseThrow();
        return TattooWork.toTattooWorksResponseDto(tattooWork);
    }
}
