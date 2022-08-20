package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.tattooWork.models.Currency;
import com.example.tattooartistbackend.tattooWork.models.TattooWorkPatchRequestDto;
import com.example.tattooartistbackend.tattooWork.models.TattooWorkPostRequestDto;
import com.example.tattooartistbackend.tattooWork.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.user.User;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
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
    private User madeBy;
    private BigDecimal price;
    @ManyToOne
    private User client;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private String coverPhoto;
    @ElementCollection
    private List<String> photos;
    private Integer like_number;
    private Integer dislike_number;
    private String description;
    @OneToOne(cascade = CascadeType.REMOVE)
    private Comment comment;// will be posted under tattoo-work by the person who had the tattoo but like and dislike will be able to given by anybody else

    public static TattooWork fromTattooWorkPostRequest(TattooWorkPostRequestDto tattooWorkPostRequestDto, User client, User madeBy) {
        return TattooWork.builder()
                .client(client)
                .comment(null)
                .coverPhoto(tattooWorkPostRequestDto.getCoverPhoto())
                .currency(tattooWorkPostRequestDto.getCurrency())
                .description(tattooWorkPostRequestDto.getDescription())
                .dislike_number(0)
                .like_number(0)
                .madeBy(madeBy)
                .photos(tattooWorkPostRequestDto.getPhotos())
                .price(tattooWorkPostRequestDto.getPrice())
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
        res.setDislikeNumber(tattooWork.getDislike_number());
        res.setLikeNumber(tattooWork.getLike_number());
        res.setMadeById(tattooWork.getMadeBy().getId());
        return res;
    }


//    public static TattooWork fromTattooWorkPatchRequest(TattooWorkPatchRequestDto tattooWorkPatchRequestDto){
//        return TattooWork.builder()
//                .client(client)
//                .comment(comment)
//                .coverPhoto(tattooWorkPostRequestDto.getCoverPhoto())
//                .currency(tattooWorkPostRequestDto.getCurrency())
//                .description(tattooWorkPostRequestDto.getDescription())
//                .dislike_number(0)
//                .like_number(0)
//                .madeBy(madeBy)
//                .photos(tattooWorkPostRequestDto.getPhotos())
//                .price(tattooWorkPostRequestDto.getPrice())
//                .build();
//    }
}
