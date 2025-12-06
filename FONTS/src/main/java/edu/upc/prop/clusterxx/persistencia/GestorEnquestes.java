package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * Gestor encarregat de la persistència de les enquestes en fitxers JSON.
 * Segueix l'estructura jeràrquica:
 * - dades/enquestes/index.json: [{ id, titol, creador, numPreguntes, numParticipants }]
 * - dades/enquestes/{id_enquesta}/enquesta.json: definició bàsica (títol, descripció, creador)
 * - dades/enquestes/{id_enquesta}/preguntes/index.json: [{ id, text, tipus }]
 * - dades/enquestes/{id_enquesta}/preguntes/{id_pregunta}.json: pregunta completa
 * - dades/enquestes/{id_enquesta}/respostes/index.json: ["usuari1", "usuari2", ...]
 * - dades/enquestes/{id_enquesta}/respostes/{username}.json: respostes de l'usuari
 */
public class GestorEnquestes {
    private static final String DIRECTORI_ENQUESTES = "dades/enquestes";
    private static final String FITXER_INDEX = "index.json";
    private static final String FITXER_ENQUESTA = "enquesta.json";
    private static final String DIR_PREGUNTES = "preguntes";
    private static final String DIR_RESPOSTES = "respostes";

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
     * Guarda una única enquesta seguint l'estructura jeràrquica:
     * - Crea directori {id_enquesta}/
     * - Guarda enquesta.json amb dades bàsiques (títol, descripció, creador)
     * - Les preguntes i respostes es gestionen via GestorPreguntes i GestorRespostes
     * 
     * @param enquesta L'enquesta a guardar
     * @throws IOException Si hi ha error d'escriptura
     */
    public void guardarEnquesta(Enquesta enquesta) throws IOException {
        // Crear directori de l'enquesta
        File dirEnquesta = new File(DIRECTORI_ENQUESTES, enquesta.getId());
        if (!dirEnquesta.exists()) {
            dirEnquesta.mkdirs();
        }
        
        // Crear subdirectoris per preguntes i respostes
        File dirPreguntes = new File(dirEnquesta, DIR_PREGUNTES);
        File dirRespostes = new File(dirEnquesta, DIR_RESPOSTES);
        if (!dirPreguntes.exists()) dirPreguntes.mkdirs();
        if (!dirRespostes.exists()) dirRespostes.mkdirs();
        
        // Guardar només dades bàsiques de l'enquesta
        JSONObject jsonEnquesta = new JSONObject();
        jsonEnquesta.put("id", enquesta.getId());
        jsonEnquesta.put("titol", enquesta.getTitol());
        jsonEnquesta.put("descripcio", enquesta.getDescripcio());
        jsonEnquesta.put("idCreador", enquesta.getIdCreador());
        
        // Guardar participants
        JSONArray jsonParticipants = new JSONArray(enquesta.getParticipants());
        jsonEnquesta.put("participants", jsonParticipants);
        
        File fitxerEnquesta = new File(dirEnquesta, FITXER_ENQUESTA);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fitxerEnquesta))) {
            writer.write(jsonEnquesta.toString(4));
        }
    }

    /**
     * Guarda l'índex amb les dades bàsiques de totes les enquestes.
     * Format: [{ id, titol, creador, numPreguntes, numParticipants }]
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
            entry.put("creador", enquesta.getIdCreador());
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
     * Elimina el directori complet d'una enquesta (incloent preguntes i respostes).
     * 
     * @param idEnquesta L'ID de l'enquesta a eliminar
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarFitxerEnquesta(String idEnquesta) {
        File dirEnquesta = new File(DIRECTORI_ENQUESTES, idEnquesta);
        return eliminarDirectoriRecursiu(dirEnquesta);
    }
    
    /**
     * Elimina un directori i tot el seu contingut de forma recursiva.
     */
    private boolean eliminarDirectoriRecursiu(File dir) {
        if (!dir.exists()) {
            return false;
        }
        
        if (dir.isDirectory()) {
            File[] fitxers = dir.listFiles();
            if (fitxers != null) {
                for (File fitxer : fitxers) {
                    eliminarDirectoriRecursiu(fitxer);
                }
            }
        }
        
        return dir.delete();
    }

    /**
     * Carrega totes les enquestes dels seus directoris individuals.
     * Les preguntes i respostes es carreguen via GestorPreguntes i GestorRespostes.
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

        // Llistar tots els subdirectoris (cada subdirectori és una enquesta)
        File[] subdirs = dir.listFiles(File::isDirectory);
        
        if (subdirs == null) {
            return enquestes;
        }

        for (File subdir : subdirs) {
            try {
                String idEnquesta = subdir.getName();
                Enquesta enquesta = carregarEnquesta(idEnquesta, usuaris);
                if (enquesta != null) {
                    enquestes.put(enquesta.getId(), enquesta);
                }
            } catch (Exception e) {
                System.err.println("Error carregant enquesta de " + subdir.getName() + ": " + e.getMessage());
            }
        }

        return enquestes;
    }

    /**
     * Carrega una única enquesta des del seu directori jeràrquic.
     * Només carrega les dades bàsiques de l'enquesta (de enquesta.json).
     * Les preguntes i respostes es carreguen via GestorPreguntes i GestorRespostes.
     * 
     * @param idEnquesta L'ID de l'enquesta a carregar
     * @param usuaris Mapa d'usuaris existents
     * @return L'enquesta carregada o null si no existeix
     * @throws IOException Si hi ha error de lectura
     */
    public Enquesta carregarEnquesta(String idEnquesta, HashMap<String, Usuari> usuaris) throws IOException {
        File dirEnquesta = new File(DIRECTORI_ENQUESTES, idEnquesta);
        if (!dirEnquesta.exists() || !dirEnquesta.isDirectory()) {
            return null;
        }
        
        File fitxerEnquesta = new File(dirEnquesta, FITXER_ENQUESTA);
        if (!fitxerEnquesta.exists()) {
            return null;
        }
        
        // Llegir enquesta.json
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fitxerEnquesta))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        if (content.length() == 0) {
            return null;
        }

        JSONObject jsonEnquesta = new JSONObject(content.toString());
        
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
