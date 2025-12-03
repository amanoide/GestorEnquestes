package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * Gestor encarregat de la persistència de les enquestes en fitxers JSON.
 * Utilitza una estructura de directoris:
 * - dades/enquestes/index.json: Índex amb dades bàsiques de totes les enquestes (id, títol, descripció, creador, nombre de preguntes, nombre de participants)
 * - dades/enquestes/{idenquesta}.json: Fitxer individual per cada enquesta (preguntes + respostes)
 */
public class GestorEnquestes {
    private static final String DIRECTORI_ENQUESTES = "dades/enquestes";
    private static final String FITXER_INDEX = "index.json";

    /**
     * Constructor. Crea el directori d'enquestes si no existeix.
     */
    public GestorEnquestes() {
        File dir = new File(DIRECTORI_ENQUESTES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Guarda totes les enquestes: actualitza l'índex i guarda cada enquesta en el seu fitxer.
     * 
     * @param enquestes Mapa d'enquestes a guardar (id -> Enquesta)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarEnquestes(HashMap<String, Enquesta> enquestes) throws IOException {
        // Guardar l'índex
        guardarIndex(enquestes);
        
        // Guardar cada enquesta en el seu fitxer
        for (Enquesta enquesta : enquestes.values()) {
            guardarEnquesta(enquesta);
        }
    }

    /**
     * Guarda una única enquesta al seu fitxer individual.
     * 
     * @param enquesta L'enquesta a guardar
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarEnquesta(Enquesta enquesta) throws IOException {
        JSONObject jsonEnquesta = enquestAJson(enquesta);
        
        File fitxer = new File(DIRECTORI_ENQUESTES, enquesta.getId() + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonEnquesta.toString(4));
        }
    }

    /**
     * Guarda l'índex amb les dades bàsiques de totes les enquestes.
     * Permet llistar enquestes sense carregar-les totes a memòria.
     * 
     * @param enquestes Mapa d'enquestes
     * @throws IOException Si hi ha error d'escriptura
     */
    private void guardarIndex(HashMap<String, Enquesta> enquestes) throws IOException {
        JSONArray jsonArray = new JSONArray();
        
        for (Enquesta enquesta : enquestes.values()) {
            JSONObject entry = new JSONObject();
            entry.put("id", enquesta.getId());
            entry.put("titol", enquesta.getTitol());
            entry.put("descripcio", enquesta.getDescripcio());
            entry.put("idCreador", enquesta.getIdCreador());
            entry.put("numPreguntes", enquesta.getPreguntes().size());
            entry.put("numParticipants", enquesta.getNumParticipants());
            jsonArray.put(entry);
        }
        
        File fitxer = new File(DIRECTORI_ENQUESTES, FITXER_INDEX);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxer))) {
            writer.write(jsonArray.toString(4));
        }
    }

    /**
     * Elimina el fitxer d'una enquesta.
     * 
     * @param idEnquesta L'ID de l'enquesta a eliminar
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarFitxerEnquesta(String idEnquesta) {
        File fitxer = new File(DIRECTORI_ENQUESTES, idEnquesta + ".json");
        return fitxer.delete();
    }

    /**
     * Converteix una enquesta a JSONObject.
     */
    private JSONObject enquestAJson(Enquesta enquesta) {
        JSONObject jsonEnquesta = new JSONObject();
        jsonEnquesta.put("id", enquesta.getId());
        jsonEnquesta.put("titol", enquesta.getTitol());
        jsonEnquesta.put("descripcio", enquesta.getDescripcio());
        jsonEnquesta.put("idCreador", enquesta.getIdCreador());

        // Guardar preguntes
        JSONArray jsonPreguntes = new JSONArray();
        for (Pregunta pregunta : enquesta.getPreguntes()) {
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

            // Guardar respostes de la pregunta
            JSONArray jsonRespostes = new JSONArray();
            for (Resposta resposta : pregunta.getRespostes().values()) {
                JSONObject jsonResposta = new JSONObject();
                jsonResposta.put("id", resposta.getId());
                jsonResposta.put("username", resposta.getUsernameUsuari());
                jsonResposta.put("text", resposta.getTextResposta());
                jsonRespostes.put(jsonResposta);
            }
            jsonPregunta.put("respostes", jsonRespostes);

            jsonPreguntes.put(jsonPregunta);
        }
        jsonEnquesta.put("preguntes", jsonPreguntes);

        // Guardar participants
        JSONArray jsonParticipants = new JSONArray(enquesta.getParticipants());
        jsonEnquesta.put("participants", jsonParticipants);

        return jsonEnquesta;
    }

    /**
     * Carrega totes les enquestes dels fitxers individuals.
     * 
     * @param usuaris Mapa d'usuaris existents per vincular creadors
     * @return HashMap d'enquestes carregades (id -> Enquesta)
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Enquesta> carregarEnquestes(HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, Enquesta> enquestes = new HashMap<>();
        File dir = new File(DIRECTORI_ENQUESTES);

        if (!dir.exists()) {
            return enquestes;
        }

        // Llistar tots els fitxers .json excepte index.json
        File[] fitxers = dir.listFiles((d, name) -> name.endsWith(".json") && !name.equals(FITXER_INDEX));
        
        if (fitxers == null) {
            return enquestes;
        }

        for (File fitxer : fitxers) {
            try {
                Enquesta enquesta = carregarEnquesta(fitxer, usuaris);
                if (enquesta != null) {
                    enquestes.put(enquesta.getId(), enquesta);
                }
            } catch (Exception e) {
                System.err.println("Error carregant enquesta de " + fitxer.getName() + ": " + e.getMessage());
            }
        }

        return enquestes;
    }

    /**
     * Carrega una única enquesta des del seu fitxer.
     * 
     * @param idEnquesta L'ID de l'enquesta a carregar
     * @param usuaris Mapa d'usuaris existents
     * @return L'enquesta carregada o null si no existeix
     * @throws IOException Si hi ha error de lectura
     */
    public Enquesta carregarEnquesta(String idEnquesta, HashMap<String, Usuari> usuaris) throws IOException {
        File fitxer = new File(DIRECTORI_ENQUESTES, idEnquesta + ".json");
        if (!fitxer.exists()) {
            return null;
        }
        return carregarEnquesta(fitxer, usuaris);
    }

    /**
     * Carrega una enquesta des d'un fitxer específic.
     */
    private Enquesta carregarEnquesta(File fitxer, HashMap<String, Usuari> usuaris) throws IOException {
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

        JSONObject jsonEnquesta = new JSONObject(content.toString());
        return jsonAEnquesta(jsonEnquesta, usuaris);
    }

    /**
     * Converteix un JSONObject a Enquesta.
     */
    private Enquesta jsonAEnquesta(JSONObject jsonEnquesta, HashMap<String, Usuari> usuaris) {
        String id = jsonEnquesta.getString("id");
        String titol = jsonEnquesta.getString("titol");
        String descripcio = jsonEnquesta.getString("descripcio");
        String idCreador = jsonEnquesta.getString("idCreador");

        // Recuperar l'objecte usuari creador si existeix
        Usuari creador = null;
        if (usuaris != null) {
            creador = usuaris.get(idCreador);
        }
        if (creador == null) {
            creador = new Usuari(idCreador, "unknown");
        }

        Enquesta enquesta = new Enquesta(id, titol, descripcio, creador);

        // Carregar preguntes
        JSONArray jsonPreguntes = jsonEnquesta.getJSONArray("preguntes");
        for (int j = 0; j < jsonPreguntes.length(); j++) {
            JSONObject jsonPregunta = jsonPreguntes.getJSONObject(j);
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

            // Carregar respostes
            if (jsonPregunta.has("respostes")) {
                JSONArray jsonRespostes = jsonPregunta.getJSONArray("respostes");
                for (int k = 0; k < jsonRespostes.length(); k++) {
                    JSONObject jsonResposta = jsonRespostes.getJSONObject(k);
                    String idResposta = jsonResposta.getString("id");
                    String username = jsonResposta.getString("username");
                    String textResposta = jsonResposta.getString("text");

                    Usuari dummyUser = new Usuari(username, "");
                    Resposta resposta = new Resposta(idResposta, idPregunta, textResposta, dummyUser);
                    pregunta.afegirResposta(username, resposta);

                    // Vincular a l'usuari si el tenim
                    if (usuaris != null) {
                        Usuari u = usuaris.get(username);
                        if (u != null) {
                            u.afegirResposta(resposta.getId(), resposta);
                        }
                    }
                }
            }

            enquesta.afegirPregunta(pregunta);
        }

        // Carregar participants
        if (jsonEnquesta.has("participants")) {
            JSONArray jsonParticipants = jsonEnquesta.getJSONArray("participants");
            for (int k = 0; k < jsonParticipants.length(); k++) {
                enquesta.registrarParticipacio(jsonParticipants.getString(k));
            }
        }

        // Vincular enquesta a l'usuari creador
        if (creador != null) {
            creador.addEnquestaCreada(enquesta);
        }

        return enquesta;
    }

    /**
     * Obté les dades bàsiques de totes les enquestes sense carregar-les completament.
     * 
     * @return JSONArray amb les metadades de cada enquesta
     * @throws IOException Si hi ha error de lectura
     */
    public JSONArray obtenirIndexEnquestes() throws IOException {
        File fitxer = new File(DIRECTORI_ENQUESTES, FITXER_INDEX);
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
