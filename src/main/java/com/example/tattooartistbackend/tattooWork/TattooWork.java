package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.comment.Comment;
import com.example.tattooartistbackend.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Currency;
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
    private Double price;
    private Currency currency;
    private String coverPhoto;
    @ElementCollection
    private List<String> photos;
    private Integer like;//
    private Integer dislike;
    @OneToOne
    private Comment comment;// will be posted under tattoo-work by the person who had the tattoo but like and dislike will be able to given by anybody else

}
