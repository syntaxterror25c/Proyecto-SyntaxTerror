package pixelgym.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.Getter;

import java.io.FileInputStream;

public class FirebaseConfig {
    @Getter
    public static Firestore db;

    public static void inicializar() throws Exception {

        System.out.println("Buscando en: " + System.getProperty("user.dir"));
        FileInputStream serviceAccount = new FileInputStream("service-account.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        db = FirestoreClient.getFirestore();
    }

}