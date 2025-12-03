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
 */
public class GestorUsuaris {
    private static final String DIRECTORI_DATA = "dades";
    private static final String FITXER_USUARIS = "dades/usuaris.json";

    /**
     * Constructor. Crea el directori data si no existeix.
     */
    public GestorUsuaris() {
        File dir = new File(DIRECTORI_DATA);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Guarda tots els usuaris al fitxer JSON.
     * 
     * @param usuaris Mapa d'usuaris a guardar (username -> Usuari)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarUsuaris(HashMap<String, Usuari> usuaris) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (Usuari usuari : usuaris.values()) {
            JSONObject jsonUsuari = new JSONObject();
            jsonUsuari.put("username", usuari.getUsername());
            jsonUsuari.put("password", usuari.getPassword());

            jsonArray.put(jsonUsuari);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FITXER_USUARIS))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Carrega tots els usuaris del fitxer JSON.
     * 
     * @return Mapa d'usuaris carregats (username -> Usuari)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Usuari> carregarUsuaris() throws IOException {
        HashMap<String, Usuari> usuaris = new HashMap<>();
        File file = new File(FITXER_USUARIS);

        if (!file.exists()) {
            return usuaris;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0)
            return usuaris;

        JSONArray jsonArray = new JSONArray(content.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonUsuari = jsonArray.getJSONObject(i);
            String username = jsonUsuari.getString("username");
            String password = jsonUsuari.optString("password", "default");

            Usuari usuari = new Usuari(username, password);
            usuaris.put(username, usuari);
        }

        return usuaris;
    }
}
