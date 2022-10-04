package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.configuration.MailSenderService;
import com.example.tattooartistbackend.exceptions.NotOwnerOfEntityException;
import com.example.tattooartistbackend.exceptions.ReviewNotFoundException;
import com.example.tattooartistbackend.exceptions.TattooWorkNotFoundException;
import com.example.tattooartistbackend.exceptions.UserArtistPageNotFoundException;
import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.TattooWorkPatchRequestDto;
import com.example.tattooartistbackend.generated.models.TattooWorkPostRequestDto;
import com.example.tattooartistbackend.generated.models.TattooWorkResponsePageable;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkDocument;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsRepository;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TattooWorkService {
    private final TattooWorkRepository tattooWorkRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SecurityService securityService;
    private final MailSenderService mailSenderService;
    private final RoleService roleService;
    private final TattooWorkEsRepository tattooWorkEsRepository;

    public TattooWorksResponseDto createTattooWork(TattooWorkPostRequestDto tattooWorkPostRequestDto) {
        try {
            var client = userRepository.findById(tattooWorkPostRequestDto.getClientId()).orElseThrow(UserNotFoundException::new);
            var madeBy = securityService.getUser();
            if (!madeBy.isHasArtistPage()) {
                try {
                    throw new UserArtistPageNotFoundException();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            var convertedPrice = getConvertedPrice(tattooWorkPostRequestDto.getCurrency(), tattooWorkPostRequestDto.getPrice());
            System.out.println("CURRENCY"+convertedPrice);
            var tattooWork = tattooWorkRepository.save(TattooWork.fromTattooWorkPostRequest(tattooWorkPostRequestDto, client, madeBy, convertedPrice));
            var informationMessage=
                    "Hi,\""+ client.getFirstName() +" "+client.getLastName() +"\""+ '\n'
                            +" Tattoo Artist "+madeBy.getFirstName() + " "+  madeBy.getLastName() +" Published a tattoo work which is made on you. Now you can go to link and add your feelings about the work as comment and give a rating to help others to see Tattoo Arist average rating." + '\n' +'\n'+
                            "Link : http://localhost:4200/tattoo-work/"+tattooWork.getId()+'\n'
                            +"Thank you!";
            mailSenderService.sendSimpleMessage(client.getEmail(),informationMessage);
            tattooWorkEsRepository.save(TattooWorkDocument.fromTattooWork(tattooWork));
            return TattooWork.toTattooWorksResponseDto(tattooWork);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }



    private BigDecimal getConvertedPrice(Currency currency, BigDecimal price) {
        var url = "https://api.exchangerate.host/latest?base="+currency+"&amount=" + price+"&symbols=EUR";
        var body = restTemplate.getForObject(url ,String.class);
        System.out.println(body);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return BigDecimal.valueOf(jsonNode.get("rates").get("EUR").asDouble());
    }

    public void deleteTattooWork(UUID id) {
        var authenticatedUser = securityService.getUser();
        var tattooWork = tattooWorkRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
        if (!authenticatedUser.getId().equals(tattooWork.getMadeBy().getId()) && !roleService.isAdmin(authenticatedUser.getUid())) {
            throw new NotOwnerOfEntityException("only the owner can delete the tattooWork!");
        } else {
            if (tattooWork.getFavoriteUserList() != null) {
                var userDetails = tattooWork.getFavoriteUserList();
                for (User user : userDetails) {
                    var list = new ArrayList<>(user.getFavoriteTattooWorks());
                    list.remove(tattooWork);
                    user.setFavoriteTattooWorks(list);
                    userRepository.save(user);
                }
            }
            tattooWorkRepository.deleteById(id);
            tattooWorkEsRepository.deleteById(id);

        }
    }

    public TattooWorkResponsePageable getAllTattooWorks(Integer page, Integer size, BigDecimal price, String country) {
        Pageable pageable = PageRequest.of(page, size);
        System.out.println(tattooWorkRepository.findAllByPriceGreaterThan(price, pageable).getTotalElements());
        var list = tattooWorkRepository.findAllByPriceGreaterThan(price, pageable)
                .getContent()
                .stream()
                .map(TattooWork::toTattooWorksResponseDto)
                .collect(Collectors.toList());
        TattooWorkResponsePageable tattooWorkResponsePageable=new TattooWorkResponsePageable();
        tattooWorkResponsePageable.setTattooWorks(list);
        tattooWorkResponsePageable.setTotalElements((int) tattooWorkRepository.findAllByPriceGreaterThan(price, pageable).getTotalElements());
        return tattooWorkResponsePageable;
    }

    public TattooWorksResponseDto patchTattooWork(UUID id, TattooWorkPatchRequestDto tattooWorkPatchRequestDto) {
        var tattooWork = tattooWorkRepository.findById(id).orElseThrow(TattooWorkNotFoundException::new);
        var authenticatedUser = securityService.getUser();

        System.out.println(authenticatedUser.getId());
        System.out.println(tattooWork.getMadeBy().getId());

        if (authenticatedUser.getId().toString()==tattooWork.getMadeBy().getId().toString()) {
            throw new NotOwnerOfEntityException("only the owner can edit the tattooWork!");
        }
        tattooWork.setDescription(tattooWorkPatchRequestDto.getDescription());
        tattooWork.setPrice(tattooWorkPatchRequestDto.getPrice());
        tattooWork.setCurrency(tattooWorkPatchRequestDto.getCurrency());
        tattooWork.setPhotos(tattooWorkPatchRequestDto.getPhotos());
        tattooWork.setCoverPhoto(tattooWorkPatchRequestDto.getCoverPhoto());
        var convertedPrice = getConvertedPrice(tattooWorkPatchRequestDto.getCurrency(), tattooWorkPatchRequestDto.getPrice());
        tattooWork.setConvertedPriceValue(convertedPrice);

        tattooWorkEsRepository.save(TattooWorkDocument.fromTattooWork(tattooWork));
        tattooWorkRepository.save(tattooWork);

        return TattooWork.toTattooWorksResponseDto(tattooWork);
    }

    public TattooWorksResponseDto getTattooWorkById(UUID id) {
        var tattooWork = tattooWorkRepository.findById(id).orElseThrow(TattooWorkNotFoundException::new);
        return TattooWork.toTattooWorksResponseDto(tattooWork);
    }
}
