package pixelgym.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pixelgym.dao.*;
import pixelgym.dao.impl.*;
import pixelgym.model.*;
import java.util.List;

public class MainController {

    // Tablas
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableView<Sesion> tablaSesiones;
    @FXML private TableView<Reserva> tablaReservas;

    // Inputs
    @FXML private TextField txtEmailRecarga;
    @FXML private TextField txtCreditosNuevos;
    @FXML private TextField txtBusquedaReserva;

    // DAOs (Instanciados igual que en tu PixelGymAdmin)
    private final IUsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final ISesionDAO sesionDAO = new SesionDAOImpl();
    private final IReservaDAO reservaDAO = new ReservaDAOImpl();

    @FXML
    public void initialize() {
        configurarTablas();
    }

    private void configurarTablas() {
        // --- COLUMNAS USUARIOS ---
        TableColumn<Usuario, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nombre_usuario"));

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Usuario, String> colPlan = new TableColumn<>("Plan");
        colPlan.setCellValueFactory(new PropertyValueFactory<>("nombrePlan")); // Usa tu método getNombrePlan()

        TableColumn<Usuario, Long> colCred = new TableColumn<>("Créditos");
        colCred.setCellValueFactory(new PropertyValueFactory<>("creditos")); // Usa tu método getCreditos()

        tablaUsuarios.getColumns().addAll(colNom, colEmail, colPlan, colCred);

        // --- COLUMNAS SESIONES ---
        TableColumn<Sesion, String> colAct = new TableColumn<>("Actividad");
        colAct.setCellValueFactory(new PropertyValueFactory<>("nombre_actividad"));

        TableColumn<Sesion, String> colProf = new TableColumn<>("Profesor");
        colProf.setCellValueFactory(new PropertyValueFactory<>("nombre_profesor"));

        TableColumn<Sesion, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<Sesion, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora_inicio"));

        tablaSesiones.getColumns().addAll(colAct, colProf, colFecha, colHora);

        // --- COLUMNAS RESERVAS ---
        TableColumn<Reserva, String> colResUser = new TableColumn<>("ID Usuario (UID)");
        colResUser.setCellValueFactory(new PropertyValueFactory<>("uid"));

        TableColumn<Reserva, String> colResAct = new TableColumn<>("Actividad");
        colResAct.setCellValueFactory(new PropertyValueFactory<>("nombre_actividad"));

        TableColumn<Reserva, String> colResEstado = new TableColumn<>("Estado");
        colResEstado.setCellValueFactory(new PropertyValueFactory<>("estado_reserva"));

        tablaReservas.getColumns().addAll(colResUser, colResAct, colResEstado);
    }

    @FXML
    private void handleListarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            tablaUsuarios.getItems().setAll(usuarios);
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }

    @FXML
    private void handleRecargaCreditos() {
        String email = txtEmailRecarga.getText();
        String credStr = txtCreditosNuevos.getText();

        if (email.isEmpty() || credStr.isEmpty()) {
            mostrarError("Datos incompletos", "Por favor, rellena email y créditos.");
            return;
        }

        try {
            long nuevosCreditos = Long.parseLong(credStr);
            usuarioDAO.actualizarCreditos(email, nuevosCreditos);
            mostrarInfo("Éxito", "Créditos actualizados para " + email);
            handleListarUsuarios(); // Refrescar
        } catch (NumberFormatException e) {
            mostrarError("Error", "La cantidad de créditos debe ser un número.");
        } catch (Exception e) {
            mostrarError("Error Firebase", e.getMessage());
        }
    }

    @FXML
    private void handleListarSesiones() {
        try {
            tablaSesiones.getItems().setAll(sesionDAO.obtenerTodas());
        } catch (Exception e) {
            mostrarError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleListarReservas() {
        try {
            // Ejemplo: Listar todas por ahora
            tablaReservas.getItems().setAll(reservaDAO.obtenerTodas());
        } catch (Exception e) {
            mostrarError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleAltaMasiva() {
        mostrarInfo("Función", "Aquí podrías abrir un diálogo con el formulario de alta masiva.");
    }

    private void mostrarError(String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error en la aplicación");
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}