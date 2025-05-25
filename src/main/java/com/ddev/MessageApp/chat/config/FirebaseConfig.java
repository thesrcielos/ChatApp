package com.ddev.MessageApp.chat.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_BUCKET}")
    private String bucketName;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-credentials.json");

        Objects.requireNonNull(serviceAccount, "ServiceAccount cant be null");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(bucketName)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}