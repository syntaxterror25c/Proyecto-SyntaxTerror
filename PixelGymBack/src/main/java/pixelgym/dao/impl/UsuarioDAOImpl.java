package pixelgym.dao.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import pixelgym.config.FirebaseConfig;
import pixelgym.dao.IUsuarioDAO;
import pixelgym.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {
    private final CollectionReference collection;

    public UsuarioDAOImpl() {
        // Usamos el método getDb() de tu clase FirebaseConfig
        this.collection = FirebaseConfig.getDb().collection("usuarios");
    }

    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = collection.get();
        for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
            lista.add(doc.toObject(Usuario.class));
        }
        return lista;
    }

    @Override
    public void actualizarCreditos(String email, long nuevosCreditos) throws Exception {
        String uid = buscarUsuarioPorEmail(email);
        collection.document(uid).update("suscripcion_actual.creditos", nuevosCreditos).get();
    }

    @Override
    public String buscarUsuarioPorEmail(String email) throws Exception {
        // Buscamos el documento donde el campo "email" coincida
        QuerySnapshot query = collection.whereEqualTo("email", email).get().get();

        if (query.isEmpty()) {
            return null; // Si no hay resultados, devolvemos null
        }

        // Devolvemos el ID del documento (que es el UID)
        return query.getDocuments().get(0).getId();
    }

    @Override
    public Usuario buscarPorUid(String uid) throws Exception {
        // Buscamos el documento cuyo ID sea el uid
        return collection.document(uid).get().get().toObject(Usuario.class);
    }

    @Override
    public long obtenerSumaTotalCreditos() throws Exception {
        long total = 0;
        // Traemos todos los documentos de la colección usuarios
        List<QueryDocumentSnapshot> documentos = collection.get().get().getDocuments();

        for (QueryDocumentSnapshot doc : documentos) {
            // Convertimos el documento a nuestro objeto Usuario
            Usuario u = doc.toObject(Usuario.class);
            if (u != null) {
                total += u.getCreditos(); // Sumamos sus créditos
            }
        }
        return total;
    }
}