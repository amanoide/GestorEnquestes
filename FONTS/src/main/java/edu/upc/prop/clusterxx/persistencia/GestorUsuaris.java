package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.Usuari;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    /**
     * Constructor. Crea el directori d'usuaris si no existeix.
     */
    public GestorUsuaris() {
        File dir = new File(DIRECTORI_USUARIS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Guarda tots els usuaris: actualitza l'índex i guarda cada usuari en el seu fitxer.
     * 
     * @param usuaris Mapa d'usuaris a guardar (username -> Usuari)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarUsuaris(HashMap<String, Usuari> usuaris) throws IOException {
        // Guardar cada usuari en el seu fitxer
        for (Usuari usuari : usuaris.values()) {
            guardarUsuari(usuari);
        }
        // Actualitzar l'índex
        guardarIndex(usuaris);
    }

    /**
     * Guarda un únic usuari al seu fitxer individual.
     * 
     * @param usuari L'usuari a guardar
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarUsuari(Usuari usuari) throws IOException {
        File fitxer = new File(DIRECTORI_USUARIS, usuari.getUsername() + ".json");
        
        JSONObject jsonUsuari = new JSONObject();
        jsonUsuari.put("username", usuari.getUsername());
        jsonUsuari.put("password", usuari.getPassword());
        // Aquí podries afegir més camps si cal (respostes, enquestesCreades, etc.)

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonUsuari.toString(4));
        }
    }

    /**
     * Guarda l'índex amb la llista de tots els usernames.
     * 
     * @param usuaris Mapa d'usuaris
     * @throws IOException Si hi ha error d'escriptura
     */
    private void guardarIndex(HashMap<String, Usuari> usuaris) throws IOException {
        File fitxer = new File(DIRECTORI_USUARIS, FITXER_INDEX);
        JSONArray jsonArray = new JSONArray();

        for (Usuari usuari : usuaris.values()) {
            JSONObject jsonEntry = new JSONObject();
            jsonEntry.put("username", usuari.getUsername());
            jsonArray.put(jsonEntry);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Elimina el fitxer d'un usuari.
     * 
     * @param username El username de l'usuari a eliminar
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarFitxerUsuari(String username) {
        File fitxer = new File(DIRECTORI_USUARIS, username + ".json");
        return fitxer.delete();
    }

    /**
     * Carrega tots els usuaris dels fitxers JSON individuals.
     * 
     * @return Mapa d'usuaris carregats (username -> Usuari)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Usuari> carregarUsuaris() throws IOException {
        HashMap<String, Usuari> usuaris = new HashMap<>();
        File indexFile = new File(DIRECTORI_USUARIS, FITXER_INDEX);

        if (!indexFile.exists()) {
            return usuaris;
        }

        // Llegir l'índex per saber quins usuaris carregar
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0) return usuaris;

        JSONArray jsonArray = new JSONArray(content.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            String username = jsonEntry.getString("username");
            
            // Carregar l'usuari individual
            Usuari usuari = carregarUsuari(username);
            if (usuari != null) {
                usuaris.put(username, usuari);
            }
        }

        return usuaris;
    }

    /**
     * Carrega un usuari individual del seu fitxer.
     * 
     * @param username El username de l'usuari a carregar
     * @return L'usuari carregat o null si no existeix
     * @throws IOException Si hi ha error de lectura
     */
    public Usuari carregarUsuari(String username) throws IOException {
        File fitxer = new File(DIRECTORI_USUARIS, username + ".json");
        
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

        JSONObject jsonUsuari = new JSONObject(content.toString());
        String password = jsonUsuari.optString("password", "default");
        
        return new Usuari(username, password);
    }
}
