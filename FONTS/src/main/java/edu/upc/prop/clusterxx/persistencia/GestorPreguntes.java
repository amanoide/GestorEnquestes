package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gestor encarregat de la persistència de les preguntes en fitxers JSON.
 * Segueix l'estructura jeràrquica:
 * - dades/enquestes/{id_enquesta}/preguntes/index.json: [{ id, text, tipus }]
 * - dades/enquestes/{id_enquesta}/preguntes/{id_pregunta}.json: pregunta completa amb opcions
 */
public class GestorPreguntes {
    private static final String DIRECTORI_BASE = "dades/enquestes";
    private static final String SUBDIR_PREGUNTES = "preguntes";
    private static final String FITXER_INDEX = "index.json";

    /**
     * Constructor per defecte.
     */
    public GestorPreguntes() {
    }

    /**
     * Guarda totes les preguntes d'una enquesta: actualitza l'índex i guarda cada pregunta.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param preguntes ArrayList de preguntes a guardar
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarPreguntes(String idEnquesta, ArrayList<Pregunta> preguntes) throws IOException {
        File dirPreguntes = getDirPreguntes(idEnquesta);
        if (!dirPreguntes.exists()) {
            dirPreguntes.mkdirs();
        }

        // Guardar l'índex
        guardarIndex(idEnquesta, preguntes);
        
        // Guardar cada pregunta
        for (Pregunta pregunta : preguntes) {
            guardarPregunta(idEnquesta, pregunta);
        }
    }

    /**
     * Guarda una única pregunta al seu fitxer individual.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param pregunta La pregunta a guardar
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarPregunta(String idEnquesta, Pregunta pregunta) throws IOException {
        File dirPreguntes = getDirPreguntes(idEnquesta);
        if (!dirPreguntes.exists()) {
            dirPreguntes.mkdirs();
        }

        JSONObject jsonPregunta = preguntaAJson(pregunta);
        
        File fitxer = new File(dirPreguntes, pregunta.getId() + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonPregunta.toString(4));
        }
    }

    /**
     * Guarda l'índex de preguntes amb les dades bàsiques.
     * Format: [{ id, text, tipus }]
     * 
     * @param idEnquesta ID de l'enquesta
     * @param preguntes ArrayList de preguntes
     * @throws IOException Si hi ha error d'escriptura
     */
    private void guardarIndex(String idEnquesta, ArrayList<Pregunta> preguntes) throws IOException {
        JSONArray jsonArray = new JSONArray();
        
        for (Pregunta pregunta : preguntes) {
            JSONObject entry = new JSONObject();
            entry.put("id", pregunta.getId());
            entry.put("text", pregunta.getText());
            entry.put("tipus", pregunta.getTipus().toString());
            jsonArray.put(entry);
        }
        
        File dirPreguntes = getDirPreguntes(idEnquesta);
        File fitxer = new File(dirPreguntes, FITXER_INDEX);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Elimina el fitxer d'una pregunta.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param idPregunta ID de la pregunta a eliminar
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarPregunta(String idEnquesta, String idPregunta) {
        File dirPreguntes = getDirPreguntes(idEnquesta);
        File fitxer = new File(dirPreguntes, idPregunta + ".json");
        return fitxer.delete();
    }

    /**
     * Carrega totes les preguntes d'una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta
     * @return ArrayList de preguntes carregades
     * @throws IOException Si hi ha error de lectura
     */
    public ArrayList<Pregunta> carregarPreguntes(String idEnquesta) throws IOException {
        ArrayList<Pregunta> preguntes = new ArrayList<>();
        File dirPreguntes = getDirPreguntes(idEnquesta);

        if (!dirPreguntes.exists()) {
            return preguntes;
        }

        // Llistar tots els fitxers .json excepte index.json
        File[] fitxers = dirPreguntes.listFiles((d, name) -> name.endsWith(".json") && !name.equals(FITXER_INDEX));
        
        if (fitxers == null) {
            return preguntes;
        }

        for (File fitxer : fitxers) {
            try {
                Pregunta pregunta = carregarPregunta(fitxer);
                if (pregunta != null) {
                    preguntes.add(pregunta);
                }
            } catch (Exception e) {
                System.err.println("Error carregant pregunta de " + fitxer.getName() + ": " + e.getMessage());
            }
        }

        return preguntes;
    }

    /**
     * Carrega totes les preguntes de totes les enquestes passades i les retorna en un mapa
     * on la clau és l'ID de la pregunta.
     * <p>
     * Aquest mètode actua com a adaptador per a les crides que volen un mapa global de preguntes
     * a partir d'un mapa d'enquestes carregades.
     * </p>
     * 
     * @param enquestes Mapa d'enquestes de les quals carregar preguntes
     * @return HashMap amb totes les preguntes indexades per ID
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Pregunta> carregarPreguntes(HashMap<String, Enquesta> enquestes) throws IOException {
        HashMap<String, Pregunta> all = new HashMap<>();
        if (enquestes == null) return all;

        for (String idEnquesta : enquestes.keySet()) {
            try {
                ArrayList<Pregunta> l = carregarPreguntes(idEnquesta);
                for (Pregunta p : l) {
                    if (p != null) all.put(p.getId(), p);
                }
            } catch (Exception e) {
                System.err.println("Error carregant preguntes per enquesta " + idEnquesta + ": " + e.getMessage());
            }
        }
        return all;
    }

    /**
     * Carrega una única pregunta des del seu fitxer.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param idPregunta ID de la pregunta
     * @return La pregunta carregada o null si no existeix
     * @throws IOException Si hi ha error de lectura
     */
    public Pregunta carregarPregunta(String idEnquesta, String idPregunta) throws IOException {
        File dirPreguntes = getDirPreguntes(idEnquesta);
        File fitxer = new File(dirPreguntes, idPregunta + ".json");
        
        if (!fitxer.exists()) {
            return null;
        }
        
        return carregarPregunta(fitxer);
    }

    /**
     * Carrega una pregunta des d'un fitxer específic.
     * 
     * @param fitxer El fitxer JSON del qual carregar la pregunta
     * @return La pregunta carregada o null si el fitxer està buit
     * @throws IOException Si hi ha error de lectura
     */
    private Pregunta carregarPregunta(File fitxer) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fitxer))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0) {
            return null;
        }

        JSONObject jsonPregunta = new JSONObject(content.toString());
        return jsonAPregunta(jsonPregunta);
    }

    /**
     * Converteix una pregunta a JSONObject.
     * 
     * @param pregunta La pregunta a convertir
     * @return JSONObject amb les dades de la pregunta
     */
    private JSONObject preguntaAJson(Pregunta pregunta) {
        JSONObject jsonPregunta = new JSONObject();
        jsonPregunta.put("id", pregunta.getId());
        jsonPregunta.put("text", pregunta.getText());
        jsonPregunta.put("tipus", pregunta.getTipus().toString());

        // Camps específics segons tipus
        if (pregunta.getTipus() == TipusPregunta.NUMERICA) {
            if (pregunta.getValorMinim() != null)
                jsonPregunta.put("valorMinim", pregunta.getValorMinim());
            if (pregunta.getValorMaxim() != null)
                jsonPregunta.put("valorMaxim", pregunta.getValorMaxim());
        } else if (pregunta.tipusAdmetOpcions()) {
            // Guardar opcions
            JSONArray jsonOpcions = new JSONArray();
            for (Opcio opcio : pregunta.getOpcions()) {
                JSONObject jsonOpcio = new JSONObject();
                jsonOpcio.put("id", opcio.getId());
                jsonOpcio.put("text", opcio.getText());
                if (opcio.getOrdre() != null) {
                    jsonOpcio.put("ordre", opcio.getOrdre());
                }
                jsonOpcions.put(jsonOpcio);
            }
            jsonPregunta.put("opcions", jsonOpcions);

            if (pregunta.getTipus() == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE) {
                jsonPregunta.put("maxSeleccions", pregunta.getMaxSeleccions());
            }
        }

        return jsonPregunta;
    }

    /**
     * Converteix un JSONObject a Pregunta.
     * 
     * @param jsonPregunta El JSONObject amb les dades de la pregunta
     * @return La pregunta reconstruïda a partir del JSON
     */
    private Pregunta jsonAPregunta(JSONObject jsonPregunta) {
        String idPregunta = jsonPregunta.getString("id");
        String textPregunta = jsonPregunta.getString("text");
        String tipusStr = jsonPregunta.getString("tipus");

        Pregunta pregunta = new Pregunta(idPregunta, textPregunta, tipusStr);

        // Configurar camps específics
        if (pregunta.getTipus() == TipusPregunta.NUMERICA) {
            Double min = jsonPregunta.has("valorMinim") ? jsonPregunta.getDouble("valorMinim") : null;
            Double max = jsonPregunta.has("valorMaxim") ? jsonPregunta.getDouble("valorMaxim") : null;
            pregunta.setRangNumeric(min, max);
        } else if (pregunta.tipusAdmetOpcions()) {
            if (jsonPregunta.has("opcions")) {
                JSONArray jsonOpcions = jsonPregunta.getJSONArray("opcions");
                for (int k = 0; k < jsonOpcions.length(); k++) {
                    JSONObject jsonOpcio = jsonOpcions.getJSONObject(k);
                    int idOpcio = jsonOpcio.getInt("id");
                    String textOpcio = jsonOpcio.getString("text");
                    Integer ordre = jsonOpcio.has("ordre") ? jsonOpcio.getInt("ordre") : null;

                    Opcio opcio = new Opcio(idOpcio, textOpcio, ordre);
                    pregunta.afegirOpcio(opcio);
                }
            }
            if (pregunta.getTipus() == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE
                    && jsonPregunta.has("maxSeleccions")) {
                pregunta.setMaxSeleccions(jsonPregunta.getInt("maxSeleccions"));
            }
        }

        return pregunta;
    }

    /**
     * Obté el directori de preguntes per una enquesta.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return El File del directori de preguntes
     */
    private File getDirPreguntes(String idEnquesta) {
        return new File(DIRECTORI_BASE + File.separator + idEnquesta + File.separator + SUBDIR_PREGUNTES);
    }

    /**
     * Obté les dades bàsiques de totes les preguntes sense carregar-les completament.
     * 
     * @param idEnquesta ID de l'enquesta
     * @return JSONArray amb les metadades de cada pregunta
     * @throws IOException Si hi ha error de lectura
     */
    public JSONArray obtenirIndexPreguntes(String idEnquesta) throws IOException {
        File dirPreguntes = getDirPreguntes(idEnquesta);
        File fitxer = new File(dirPreguntes, FITXER_INDEX);
        
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

