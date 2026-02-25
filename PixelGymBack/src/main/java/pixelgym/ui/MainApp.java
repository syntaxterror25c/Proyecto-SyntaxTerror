package pixelgym.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pixelgym.config.FirebaseConfig;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Inicializamos la conexión con Firebase (como hacías en consola)
            FirebaseConfig.inicializar();

            // 2. Cargamos el archivo de diseño FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
            Parent root = loader.load();

            // 3. Configuramos la ventana principal (Stage)
            Scene scene = new Scene(root);
            primaryStage.setTitle("PixelGym Admin - Sistema de Gestión");
            primaryStage.setScene(scene);

            // Opcional: Centrar la ventana y darle un tamaño mínimo
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fatal al iniciar la aplicación: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Lanzamos la aplicación JavaFX
        launch(args);
    }
}