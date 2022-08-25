package com.example.tattooartistbackend.security.config;

import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Credentials {

    private FirebaseToken decodedToken;
    private String idToken;

}
