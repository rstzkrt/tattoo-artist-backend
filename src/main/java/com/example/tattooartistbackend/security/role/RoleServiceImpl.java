package com.example.tattooartistbackend.security.role;


import com.example.tattooartistbackend.security.config.SecurityProperties;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final FirebaseAuth firebaseAuth;
    private final SecurityProperties securityProps;

    @Override
    public void addRole(String uid, Role role) throws Exception {
        try {
            UserRecord user = firebaseAuth.getUser(uid);
            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());

            if (securityProps.getValidApplicationRoles().contains(role)) {
                if (!claims.containsKey(role)) {
                    claims.put(String.valueOf(role), true);
                }

                firebaseAuth.setCustomUserClaims(uid, claims);
            } else {
                throw new Exception("Not valid Application Role entered");
            }

        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }
    }

    @Override
    public void removeRole(String uid, Role role) {
        try {
            UserRecord user = firebaseAuth.getUser(uid);
            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
            claims.remove(role);
            firebaseAuth.setCustomUserClaims(uid, claims);
        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }
    }

}