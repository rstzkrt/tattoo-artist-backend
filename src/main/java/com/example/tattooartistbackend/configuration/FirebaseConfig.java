package com.example.tattooartistbackend.configuration;

import com.example.tattooartistbackend.security.config.SecurityProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
    private final SecurityProperties secProps;
    private static final String FIREBASE_CONFIG_FILE_PATH="src/main/resources/firebase/firebase_config.json";
    @Primary
    @Bean
    public FirebaseApp getFirebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(FIREBASE_CONFIG_FILE_PATH)))
                .setDatabaseUrl(secProps.getFirebaseProps().getDatabaseUrl())
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseDatabase firebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Bean
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(getFirebaseApp());
    }

    @Bean
    public Firestore getDatabase() throws IOException {
        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(FIREBASE_CONFIG_FILE_PATH)))
                .build();
        return firestoreOptions.getService();
    }

    @Bean
    public FirebaseMessaging getMessaging() throws IOException {
        return FirebaseMessaging.getInstance(getFirebaseApp());
    }

    @Bean
    public FirebaseRemoteConfig getRemoteConfig() throws IOException {
        return FirebaseRemoteConfig.getInstance(getFirebaseApp());
    }
}