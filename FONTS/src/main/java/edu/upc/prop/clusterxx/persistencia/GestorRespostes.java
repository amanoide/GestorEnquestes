package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Gestor encarregat de la persistència de les respostes en fitxers JSON.
 * Segueix l'estructura jeràrquica:
 * - dades/enquestes/{id_enquesta}/respostes/index.json: ["usuari1", "usuari2", ...]
 * - dades/enquestes/{id_enquesta}/respostes/{username}.json: totes les respostes de l'usuari a aquesta enquesta
 */
public class GestorRespostes {
    private static final String DIRECTORI_BASE = "dades/enquestes";
    private static final String SUBDIR_RESPOSTES = "respostes";
    private static final String FITXER_INDEX = "index.json";

    /**
     * Constructor per defecte.
     */
    public GestorRespostes() {
    }

    /**
     * Guarda totes les respostes d'una enquesta: actualitza l'índex i guarda les respostes de cada usuari.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param preguntes ArrayList de preguntes (contenen les respostes)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarRespostes(String idEnquesta, ArrayList<Pregunta> preguntes) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        if (!dirRespostes.exists()) {
            dirRespostes.mkdirs();
        }

        // Agrupar respostes per usuari
        HashMap<String, HashMap<String, Resposta>> respostesPorUsuari = new HashMap<>();
        
        for (Pregunta pregunta : preguntes) {
            for (Resposta resposta : pregunta.getRespostes().values()) {
                String username = resposta.getUsernameUsuari();
                respostesPorUsuari.putIfAbsent(username, new HashMap<>());
                respostesPorUsuari.get(username).put(resposta.getId(), resposta);
            }
        }

        // Guardar l'índex
        guardarIndex(idEnquesta, respostesPorUsuari.keySet());
        
        // Guardar respostes de cada usuari
        for (Map.Entry<String, HashMap<String, Resposta>> entry : respostesPorUsuari.entrySet()) {
            guardarRespostesUsuari(idEnquesta, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Guarda les respostes d'un usuari específic a una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param username Username de l'usuari
     * @param respostes HashMap de respostes de l'usuari (id -> Resposta)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarRespostesUsuari(String idEnquesta, String username, HashMap<String, Resposta> respostes) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        if (!dirRespostes.exists()) {
            dirRespostes.mkdirs();
        }

        JSONArray jsonRespostes = new JSONArray();
        
        for (Resposta resposta : respostes.values()) {
            JSONObject jsonResposta = new JSONObject();
            jsonResposta.put("id", resposta.getId());
            jsonResposta.put("idPregunta", resposta.getIdPregunta());
            jsonResposta.put("username", resposta.getUsernameUsuari());
            jsonResposta.put("text", resposta.getTextResposta());
            jsonRespostes.put(jsonResposta);
        }
        
        File fitxer = new File(dirRespostes, username + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonRespostes.toString(4));
        }
    }

    /**
     * Guarda l'índex de respostes amb els usuaris que han participat.
     * Format: ["usuari1", "usuari2", ...]
     * 
     * @param idEnquesta ID de l'enquesta
     * @param usuaris Col·lecció d'usernames
     * @throws IOException Si hi ha error d'escriptura
     */
    private void guardarIndex(String idEnquesta, Set<String> usuaris) throws IOException {
        JSONArray jsonArray = new JSONArray();
        
        for (String username : usuaris) {
            jsonArray.put(username);
        }
        
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, FITXER_INDEX);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Elimina el fitxer de respostes d'un usuari.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param username Username de l'usuari
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarRespostesUsuari(String idEnquesta, String username) {
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, username + ".json");
        return fitxer.delete();
    }

    /**
     * Carrega les respostes d'un usuari específic.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param username Username de l'usuari
     * @param usuaris Mapa d'usuaris per vincular
     * @return HashMap de respostes (idPregunta -> Resposta)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Resposta> carregarRespostesUsuari(String idEnquesta, String username, HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, Resposta> respostes = new HashMap<>();
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, username + ".json");

        if (!fitxer.exists()) {
            return respostes;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fitxer))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0) {
            return respostes;
        }

        JSONArray jsonRespostes = new JSONArray(content.toString());
        
        for (int i = 0; i < jsonRespostes.length(); i++) {
            JSONObject jsonResposta = jsonRespostes.getJSONObject(i);
            String idResposta = jsonResposta.getString("id");
            String idPregunta = jsonResposta.getString("idPregunta");
            String usernameResposta = jsonResposta.getString("username");
            String textResposta = jsonResposta.getString("text");

            // Recuperar l'usuari si existeix
            Usuari usuari = null;
            if (usuaris != null) {
                usuari = usuaris.get(usernameResposta);
            }
            if (usuari == null) {
                usuari = new Usuari(usernameResposta, "");
            }

            Resposta resposta = new Resposta(idResposta, idPregunta, textResposta, usuari);
            respostes.put(idPregunta, resposta);  // Clau: idPregunta en lloc d'idResposta
        }

        return respostes;
    }

    /**
     * Carrega totes les respostes d'una enquesta (de tots els usuaris).
     * 
     * @param idEnquesta ID de l'enquesta
     * @param usuaris Mapa d'usuaris per vincular
     * @return HashMap d'usuaris amb les seves respostes (username -> HashMap<idPregunta, Resposta>)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, HashMap<String, Resposta>> carregarTotsRespostes(String idEnquesta, HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, HashMap<String, Resposta>> totesRespostes = new HashMap<>();
        File dirRespostes = getDirRespostes(idEnquesta);

        if (!dirRespostes.exists()) {
            return totesRespostes;
        }

        // Llistar tots els fitxers .json excepte index.json
        File[] fitxers = dirRespostes.listFiles((d, name) -> name.endsWith(".json") && !name.equals(FITXER_INDEX));
        
        if (fitxers == null) {
            return totesRespostes;
        }

        for (File fitxer : fitxers) {
            try {
                String username = fitxer.getName().replace(".json", "");
                HashMap<String, Resposta> respostes = carregarRespostesUsuari(idEnquesta, username, usuaris);
                if (!respostes.isEmpty()) {
                    totesRespostes.put(username, respostes);
                }
            } catch (Exception e) {
                System.err.println("Error carregant respostes de " + fitxer.getName() + ": " + e.getMessage());
            }
        }

        return totesRespostes;
    }

    /**
     * Obté el directori de respostes per una enquesta.
     */
    private File getDirRespostes(String idEnquesta) {
        return new File(DIRECTORI_BASE + File.separator + idEnquesta + File.separator + SUBDIR_RESPOSTES);
    }

    /**
     * Obté la llista d'usuaris que han contestat una enquesta sense carregar totes les respostes.
     * 
     * @param idEnquesta ID de l'enquesta
     * @return JSONArray amb els usernames
     * @throws IOException Si hi ha error de lectura
     */
    public JSONArray obtenirIndexRespostes(String idEnquesta) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, FITXER_INDEX);
        
        if (!fitxer.exists()) {
            return new JSONArray();
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fitxer))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0) {
            return new JSONArray();
        }

        return new JSONArray(content.toString());
    }
}

