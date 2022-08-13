package com.example.tattooartistapp.tattooWork;

import com.example.tattooartistapp.user.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
public class TattooWork {

    @Id
    private UUID id;

    @ManyToOne
    private User madeBy;


}
