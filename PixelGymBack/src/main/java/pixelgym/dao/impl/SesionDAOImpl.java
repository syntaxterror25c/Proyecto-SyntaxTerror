package pixelgym.dao.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import pixelgym.config.FirebaseConfig;
import pixelgym.dao.ISesionDAO;
import pixelgym.model.Sesion;
import java.util.ArrayList;
import java.util.List;

import static pixelgym.config.FirebaseConfig.db;

public class SesionDAOImpl implements ISesionDAO {
    private final CollectionReference collection;

    public SesionDAOImpl() {
        this.collection = FirebaseConfig.getDb().collection("sesiones");
    }

    @Override
    public List<Sesion> obtenerTodas() throws Exception {
        List<Sesion> lista = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = collection.get();

        // Obtenemos los documentos de la consulta
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot doc : documents) {
            try {
                // Convertimos el documento NoSQL a nuestro objeto Java con Lombok
                Sesion s = doc.toObject(Sesion.class);
                // Muy importante: el nombre del documento es el ID de la sesión
                s.setId(doc.getId());
                lista.add(s);
            } catch (Exception e) {
                System.err.println("Error al mapear la sesión " + doc.getId() + ": " + e.getMessage());
            }
        }
        return lista;
    }
    @Override
    public List<Sesion> buscarPorFecha(String fecha) throws Exception {
        // Filtramos en la base de datos por el campo exacto
        QuerySnapshot querySnapshot = db.collection("sesiones")
                .whereEqualTo("fecha", fecha)
                .get().get();
        return querySnapshot.toObjects(Sesion.class);
    }

    @Override
    public List<Sesion> buscarPorActividad(String nombreActividad) throws Exception {
        // Filtramos por nombre de actividad
        QuerySnapshot querySnapshot = db.collection("sesiones")
                .whereEqualTo("nombre_actividad", nombreActividad)
                .get().get();
        return querySnapshot.toObjects(Sesion.class);
    }

    @Override
    public List<Sesion> buscarPorProfesor(String nombreProfesor) throws Exception {
        return db.collection("sesiones")
                .whereEqualTo("nombre_profesor", nombreProfesor)
                .get().get().toObjects(Sesion.class);
    }

    @Override
    public List<Sesion> buscarPorSala(String sala) throws Exception {
        return db.collection("sesiones")
                .whereEqualTo("sala", sala)
                .get().get().toObjects(Sesion.class);
    }

    @Override
    public void crearSesionProgramada(Sesion s, String idPersonalizado) throws Exception {
        // .document(id) crea el documento con el nombre exacto que le enviamos
        collection.document(idPersonalizado).set(s).get();
    }
}