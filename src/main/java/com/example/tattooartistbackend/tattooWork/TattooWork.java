package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Currency;
import java.util.UUID;
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
    //list url
}
