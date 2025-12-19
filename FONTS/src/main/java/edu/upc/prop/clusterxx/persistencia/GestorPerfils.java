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
 * Segueix l'estructura jeràrquica:
 * - dades/perfils/index.json: [{ id, descripcio, idEnquesta }]
 * - dades/perfils/{id_perfil}.json: perfil complet amb clustering (si escau)
 */
public class GestorPerfils {
    private static final String DIRECTORI_PERFILS = "dades/perfils";
    private static final String FITXER_INDEX = "index.json";

    /**
     * Constructor per defecte.
     * Els directoris es crearan automàticament quan sigui necessari guardar dades.
     */
    public GestorPerfils() {
        // No crear directoris fins que sigui necessari
    }

    /**
     * Guarda tots els perfils: actualitza l'índex i guarda cada perfil en el seu fitxer.
     * 
     * @param perfils Mapa de perfils a guardar (id -> Perfil)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarPerfils(HashMap<String, Perfil> perfils) throws IOException {
        // Guardar cada perfil en el seu fitxer
        for (Perfil perfil : perfils.values()) {
            guardarPerfil(perfil);
        }
        // Actualitzar l'índex
        guardarIndex(perfils);
    }

    /**
     * Guarda un únic perfil al seu fitxer individual.
     * 
     * @param perfil El perfil a guardar
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarPerfil(Perfil perfil) throws IOException {
        // Assegurar que el directori existeix
        File dir = new File(DIRECTORI_PERFILS);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File fitxer = new File(DIRECTORI_PERFILS, perfil.getId() + ".json");

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
            jsonPerfil.put("vectorCaracteristic", new JSONArray(perfil.getVectorCaracteristic()));
            jsonPerfil.put("nomsPreguntes", new JSONArray(perfil.getNomsPreguntes()));
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonPerfil.toString(4));
        }
    }

    /**
     * Guarda l'índex amb les dades bàsiques de tots els perfils.
     * Format: [{ id, descripcio, idEnquesta }]
     * 
     * @param perfils Mapa de perfils a incloure a l'índex
     * @throws IOException Si hi ha error d'escriptura
     */
    private void guardarIndex(HashMap<String, Perfil> perfils) throws IOException {
        // Assegurar que el directori existeix
        File dir = new File(DIRECTORI_PERFILS);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File fitxer = new File(DIRECTORI_PERFILS, FITXER_INDEX);
        JSONArray jsonArray = new JSONArray();

        for (Perfil perfil : perfils.values()) {
            JSONObject jsonEntry = new JSONObject();
            jsonEntry.put("id", perfil.getId());
            jsonEntry.put("descripcion", perfil.getDescripcion());
            if (perfil.teClustering()) {
                jsonEntry.put("idEnquesta", perfil.getIdEnquesta());
            }
            jsonArray.put(jsonEntry);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Elimina el fitxer d'un perfil.
     * 
     * @param id L'ID del perfil a eliminar
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarFitxerPerfil(String id) {
        File fitxer = new File(DIRECTORI_PERFILS, id + ".json");
        return fitxer.delete();
    }

    /**
     * Carrega tots els perfils dels fitxers JSON individuals.
     * 
     * @return Mapa de perfils carregats (id -> Perfil)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Perfil> carregarPerfils() throws IOException {
        HashMap<String, Perfil> perfils = new HashMap<>();
        File indexFile = new File(DIRECTORI_PERFILS, FITXER_INDEX);

        if (!indexFile.exists()) {
            return perfils;
        }

        // Llegir l'índex per saber quins perfils carregar
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0) return perfils;

        JSONArray jsonArray = new JSONArray(content.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            Integer id = jsonEntry.getInt("id");
            
            // Carregar el perfil individual
            Perfil perfil = carregarPerfil(String.valueOf(id));
            if (perfil != null) {
                perfils.put(String.valueOf(id), perfil);
            }
        }

        return perfils;
    }

    /**
     * Carrega un perfil individual del seu fitxer.
     * 
     * @param id L'ID del perfil a carregar
     * @return El perfil carregat o null si no existeix
     * @throws IOException Si hi ha error de lectura
     */
    public Perfil carregarPerfil(String id) throws IOException {
        File fitxer = new File(DIRECTORI_PERFILS, id + ".json");
        
        if (!fitxer.exists()) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fitxer))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        JSONObject jsonPerfil = new JSONObject(content.toString());
        Integer perfilId = jsonPerfil.getInt("id");
        String descripcion = jsonPerfil.getString("descripcion");

        if (jsonPerfil.has("idEnquesta")) {
            // Perfil complet amb clustering
            String idEnquesta = jsonPerfil.getString("idEnquesta");
            Integer clusterIndex = jsonPerfil.getInt("clusterIndex");
            String clusterNom = jsonPerfil.getString("clusterNom");
            Integer clusterMida = jsonPerfil.getInt("clusterMida");
            Double clusterSilhouette = jsonPerfil.getDouble("clusterSilhouette");
            String algoritme = jsonPerfil.optString("algoritme", "KMeans");

            JSONArray jsonVector = jsonPerfil.getJSONArray("vectorCaracteristic");
            String[] vectorCaracteristic = new String[jsonVector.length()];
            for (int j = 0; j < jsonVector.length(); j++) {
                vectorCaracteristic[j] = jsonVector.getString(j);
            }

            JSONArray jsonNoms = jsonPerfil.getJSONArray("nomsPreguntes");
            List<String> nomsPreguntes = new ArrayList<>();
            for (int j = 0; j < jsonNoms.length(); j++) {
                nomsPreguntes.add(jsonNoms.getString(j));
            }

            return new Perfil(perfilId, descripcion, idEnquesta, clusterIndex, clusterNom,
                    clusterMida, clusterSilhouette, vectorCaracteristic, nomsPreguntes, algoritme);
        } else {
            return new Perfil(perfilId, descripcion);
        }
    }
}
