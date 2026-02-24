package pixelgym.dao.impl;

import com.google.cloud.firestore.*;
import pixelgym.config.FirebaseConfig;
import pixelgym.model.Reserva;
import pixelgym.dao.IReservaDAO;

import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements IReservaDAO {
    private final CollectionReference collection;

    public ReservaDAOImpl() {
        this.collection = FirebaseConfig.getDb().collection("reservas");
    }

    @Override
    public List<Reserva> obtenerTodas() throws Exception {
        return collection.get().get().toObjects(Reserva.class);
    }

    @Override
    public List<Reserva> buscarPorEmail(String email) throws Exception {
        return collection.whereEqualTo("email_usuario", email)
                .get().get().toObjects(Reserva.class);
    }

    @Override
    public List<Reserva> buscarPorFecha(String fecha) throws Exception {
        // debe llegar como "28/02/2026"
        return collection.whereEqualTo("fecha_sesion", fecha).get().get().toObjects(Reserva.class);
    }

    @Override
    public List<Reserva> buscarPorActividad(String nombreActividad) throws Exception {
        return collection.whereEqualTo("nombre_actividad", nombreActividad)
                .get().get().toObjects(Reserva.class);
    }

     @Override
    public List<Reserva> buscarPorProfesor(String nombreProfesor) throws Exception {
        List<Reserva> todas = obtenerTodas();
        List<Reserva> filtradas = new ArrayList<>();

        for (Reserva r : todas) {
            // Comprobamos que el profesor no sea nulo y comparamos en minúsculas
            if (r.getNombre_profesor() != null &&
                    r.getNombre_profesor().toLowerCase().contains(nombreProfesor.toLowerCase())) {
                filtradas.add(r);
            }
        }
        return filtradas;
    }
    @Override
    public List<Reserva> buscarPorUid(String uid) throws Exception {
        List<Reserva> lista = new ArrayList<>();

        // El primer "uid" es el nombre del campo en Firebase
        // El segundo es la variable que recibe el método
        QuerySnapshot query = collection.whereEqualTo("uid", uid).get().get();

        for (QueryDocumentSnapshot doc : query.getDocuments()) {
            lista.add(doc.toObject(Reserva.class));
        }

        return lista;
    }
}