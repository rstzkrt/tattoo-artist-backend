package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.TattooWorkPostRequestDto;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TattooWork {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    @ManyToOne
    @ToString.Exclude
    private User madeBy;
    @NotNull
    private BigDecimal price;
    @ManyToOne
    private User client;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @NotBlank
    private String coverPhoto;
    @ElementCollection
    private List<String> photos;
    @NotBlank
    private String description;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    private Comment comment;

    private BigDecimal convertedPriceValue;
    @ElementCollection
    @JsonIgnore
    private List<UUID> dislikerIds;
    @ElementCollection
    @JsonIgnore
    private List<UUID> likerIds;

    public static TattooWork fromTattooWorkPostRequest(TattooWorkPostRequestDto tattooWorkPostRequestDto, User client, User madeBy,BigDecimal convertedPriceValue) {
        return TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto(tattooWorkPostRequestDto.getCoverPhoto())
                .currency(tattooWorkPostRequestDto.getCurrency())
                .likerIds(new ArrayList<>())
                .dislikerIds(new ArrayList<>())
                .description(tattooWorkPostRequestDto.getDescription())
                .madeBy(madeBy)
                .photos(tattooWorkPostRequestDto.getPhotos())
                .price(tattooWorkPostRequestDto.getPrice())
                .convertedPriceValue(convertedPriceValue)
                .build();
    }

    public static TattooWorksResponseDto toTattooWorksResponseDto(TattooWork tattooWork) {
        TattooWorksResponseDto res = new TattooWorksResponseDto();
        res.setId(tattooWork.getId());
        res.setCurrency(tattooWork.getCurrency());
        res.setDescription(tattooWork.getDescription());
        res.setPrice(tattooWork.getPrice());
        res.setCoverPhoto(tattooWork.getCoverPhoto());
        res.setPhotos(tattooWork.getPhotos());
        res.setClientId(tattooWork.getClient().getId());
        res.setCommentId(tattooWork.getComment()==null? null :tattooWork.getComment().getId());
        res.setDislikeNumber(tattooWork.dislikerIds.size());
        res.setLikeNumber(tattooWork.likerIds.size());
        res.setMadeById(tattooWork.getMadeBy().getId());
        return res;
    }
}
