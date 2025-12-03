package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.Perfil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gestor encarregat de la persistència dels perfils en fitxers JSON.
 */
public class GestorPerfils {
    private static final String DIRECTORI_DATA = "dades";
    private static final String FITXER_PERFILS = "dades/perfils.json";

    /**
     * Constructor. Crea el directori data si no existeix.
     */
    public GestorPerfils() {
        File dir = new File(DIRECTORI_DATA);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Guarda tots els perfils al fitxer JSON.
     * 
     * @param perfils Mapa de perfils a guardar (id -> Perfil)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarPerfils(HashMap<String, Perfil> perfils) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (Perfil perfil : perfils.values()) {
            JSONObject jsonPerfil = new JSONObject();
            jsonPerfil.put("id", perfil.getId());
            jsonPerfil.put("descripcion", perfil.getDescripcion());

            if (perfil.teClustering()) {
                jsonPerfil.put("idEnquesta", perfil.getIdEnquesta());
                jsonPerfil.put("clusterIndex", perfil.getClusterIndex());
                jsonPerfil.put("clusterNom", perfil.getClusterNom());
                jsonPerfil.put("clusterMida", perfil.getClusterMida());
                jsonPerfil.put("clusterSilhouette", perfil.getClusterSilhouette());
                jsonPerfil.put("algoritme", perfil.getAlgoritme());

                // Guardar arrays/llistes
                jsonPerfil.put("vectorCaracteristic", new JSONArray(perfil.getVectorCaracteristic()));
                jsonPerfil.put("nomsPreguntes", new JSONArray(perfil.getNomsPreguntes()));
            }

            jsonArray.put(jsonPerfil);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FITXER_PERFILS))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Carrega tots els perfils del fitxer JSON.
     * 
     * @return Mapa de perfils carregats (id -> Perfil)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Perfil> carregarPerfils() throws IOException {
        HashMap<String, Perfil> perfils = new HashMap<>();
        File file = new File(FITXER_PERFILS);

        if (!file.exists()) {
            return perfils;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0)
            return perfils;

        JSONArray jsonArray = new JSONArray(content.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonPerfil = jsonArray.getJSONObject(i);
            Integer id = jsonPerfil.getInt("id");
            String descripcion = jsonPerfil.getString("descripcion");

            Perfil perfil;

            if (jsonPerfil.has("idEnquesta")) {
                // Perfil complet amb clustering
                String idEnquesta = jsonPerfil.getString("idEnquesta");
                Integer clusterIndex = jsonPerfil.getInt("clusterIndex");
                String clusterNom = jsonPerfil.getString("clusterNom");
                Integer clusterMida = jsonPerfil.getInt("clusterMida");
                Double clusterSilhouette = jsonPerfil.getDouble("clusterSilhouette");
                String algoritme = jsonPerfil.optString("algoritme", "KMeans"); // Default per compatibilitat

                // Recuperar vector característic
                JSONArray jsonVector = jsonPerfil.getJSONArray("vectorCaracteristic");
                String[] vectorCaracteristic = new String[jsonVector.length()];
                for (int j = 0; j < jsonVector.length(); j++) {
                    vectorCaracteristic[j] = jsonVector.getString(j);
                }

                // Recuperar noms preguntes
                JSONArray jsonNoms = jsonPerfil.getJSONArray("nomsPreguntes");
                List<String> nomsPreguntes = new ArrayList<>();
                for (int j = 0; j < jsonNoms.length(); j++) {
                    nomsPreguntes.add(jsonNoms.getString(j));
                }

                perfil = new Perfil(id, descripcion, idEnquesta, clusterIndex, clusterNom,
                        clusterMida, clusterSilhouette, vectorCaracteristic, nomsPreguntes, algoritme);
            } else {
                // Perfil bàsic
                perfil = new Perfil(id, descripcion);
            }

            perfils.put(String.valueOf(id), perfil);
        }

        return perfils;
    }
}
