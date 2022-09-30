package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.TattooStyle;
import com.example.tattooartistbackend.generated.models.TattooWorkPostRequestDto;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.tattooWorkReport.TattooWorkReport;
import com.example.tattooartistbackend.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
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

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @NotBlank
    private String coverPhoto;

    private BigDecimal convertedPriceValue;

    @Enumerated(EnumType.STRING)
    private TattooStyle tattooStyle;

    @ManyToOne
    @ToString.Exclude
    private User madeBy;

    @ToString.Exclude
    @ManyToOne
    private User client;

    @OneToMany
    private List<TattooWorkReport> TakenReports;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> photos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> dislikerIds;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> likerIds;

    @ManyToMany(mappedBy = "favoriteTattooWorks")
    private List<User> favoriteUserList;



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
                .tattooStyle(tattooWorkPostRequestDto.getTattooStyle())
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
        res.setClientId(tattooWork.getClient()==null? null :tattooWork.getClient().getId());
        res.setCommentId(tattooWork.getComment()==null? null :tattooWork.getComment().getId());
        res.setDislikeNumber(tattooWork.getDislikerIds().size());
        res.setLikeNumber(tattooWork.getLikerIds().size());
        res.tattooStyle(tattooWork.getTattooStyle());
        if (tattooWork.getDislikerIds()!= null){
            res.setDisLikerIds(tattooWork.getDislikerIds().stream().map(User::getId).toList());
        }else{
            res.setDisLikerIds(new ArrayList<>());
        }
        if (tattooWork.getLikerIds()!= null){
            res.setLikerIds(tattooWork.getLikerIds().stream().map(User::getId).toList());
        }else{
            res.setLikerIds(new ArrayList<>());
        }
        res.setMadeBy(tattooWork.getMadeBy().toMadeByInfoDto());
        return res;
    }
}
