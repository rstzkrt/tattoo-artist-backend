package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Currency;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class TattooWork {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;

    @ManyToOne
    private User madeBy;

    private Double price;
    private Currency currency;
}
