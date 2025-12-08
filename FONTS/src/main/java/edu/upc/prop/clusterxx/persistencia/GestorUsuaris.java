package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.Usuari;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Gestor encarregat de la persistència dels usuaris en fitxers JSON.
 * Estructura:
 * - dades/usuaris/index.json: Llista ràpida de tots els usernames
 * - dades/usuaris/{username}.json: Fitxer individual per cada usuari
 */
public class GestorUsuaris {
    private static final String DIRECTORI_USUARIS = "dades/usuaris";
    private static final String FITXER_INDEX = "index.json";

    public GestorUsuaris() {
        File dir = new File(DIRECTORI_USUARIS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Guarda tots els usuaris: actualitza l'índex y guarda cada usuario.
     */
    public void guardarUsuaris(HashMap<String, Usuari> usuaris) throws IOException {
        for (Usuari usuari : usuaris.values()) {
            guardarUsuari(usuari);
        }
        guardarIndex(usuaris);
    }

    /**
     * Guarda un únic usuari al seu fitxer individual.
     */
    public void guardarUsuari(Usuari usuari) throws IOException {
        JSONObject jsonUsuari = new JSONObject();
        jsonUsuari.put("username", usuari.getUsername());
        jsonUsuari.put("password", usuari.getPassword());
        
        JSONArray enquestesParticipades = new JSONArray();
        for (String idEnquesta : usuari.getPerfils().keySet()) {
            enquestesParticipades.put(idEnquesta);
        }
        jsonUsuari.put("enquestesParticipades", enquestesParticipades);

        Path path = Paths.get(DIRECTORI_USUARIS, usuari.getUsername() + ".json");
        Files.write(path, jsonUsuari.toString(4).getBytes());
    }

    /**
     * Guarda l'índex amb la llista de tots els usernames.
     */
    private void guardarIndex(HashMap<String, Usuari> usuaris) throws IOException {
        JSONArray jsonArray = new JSONArray();
        for (Usuari usuari : usuaris.values()) {
            JSONObject jsonEntry = new JSONObject();
            jsonEntry.put("username", usuari.getUsername());
            jsonArray.put(jsonEntry);
        }

        Path path = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        Files.write(path, jsonArray.toString(4).getBytes());
    }

    /**
     * Elimina el fitxer d'un usuari.
     */
    public boolean eliminarFitxerUsuari(String username) {
        File fitxer = new File(DIRECTORI_USUARIS, username + ".json");
        return fitxer.delete();
    }

    /**
     * Elimina un usuari del fitxer d'índex.
     * VERSIÓ OPTIMITZADA
     */
    public void eliminarUsuariDeIndex(String username) throws IOException {
        Path indexPath = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        File indexFile = indexPath.toFile();
        
        if (!indexFile.exists()) return;

        // Lectura més moderna i neta
        String content = new String(Files.readAllBytes(indexPath));
        
        if (content.isEmpty()) return;

        JSONArray jsonArray = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        boolean found = false;
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            if (!jsonEntry.getString("username").equals(username)) {
                newArray.put(jsonEntry);
            } else {
                found = true;
            }
        }

        if (found) {
            Files.write(indexPath, newArray.toString(4).getBytes());
        }
    }

    public HashMap<String, Usuari> carregarUsuaris() throws IOException {
        HashMap<String, Usuari> usuaris = new HashMap<>();
        Path indexPath = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        File indexFile = indexPath.toFile();

        if (!indexFile.exists()) return usuaris;

        String content = new String(Files.readAllBytes(indexPath));
        if (content.isEmpty()) return usuaris;

        JSONArray jsonArray = new JSONArray(content);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            String username = jsonEntry.getString("username");
            
            Usuari usuari = carregarUsuari(username);
            if (usuari != null) {
                usuaris.put(username, usuari);
            }
        }
        return usuaris;
    }

    public Usuari carregarUsuari(String username) throws IOException {
        Path path = Paths.get(DIRECTORI_USUARIS, username + ".json");
        File fitxer = path.toFile();
        
        if (!fitxer.exists()) return null;

        String content = new String(Files.readAllBytes(path));
        JSONObject jsonUsuari = new JSONObject(content);
        
        String password = jsonUsuari.optString("password", "default");
        
        return new Usuari(username, password);
    }
}