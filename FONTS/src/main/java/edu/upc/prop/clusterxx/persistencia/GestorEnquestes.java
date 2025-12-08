package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Gestor encarregat de la persistència de les enquestes.
 * Actua com a contenidor principal. Delega la gestió interna de preguntes
 * i respostes als seus respectius gestors quan és necessari carregar tot.
 */
public class GestorEnquestes {
    private static final String DIRECTORI_ENQUESTES = "dades/enquestes";
    private static final String FITXER_INDEX = "index.json";
    private static final String FITXER_ENQUESTA = "enquesta.json";
    
    // Subdirectoris que aquest gestor sap que existeixen
    private static final String DIR_PREGUNTES = "preguntes";
    private static final String DIR_RESPOSTES = "respostes";

    public GestorEnquestes() {
        File dir = new File(DIRECTORI_ENQUESTES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // -------------------------------------------------------------------------
    // GESTIÓ DE L'ENQUESTA (METADADES I ESTRUCTURA)
    // -------------------------------------------------------------------------

    /**
     * Guarda un conjunt d'enquestes (pot ser totes o només algunes modificades).
     * Actualitza l'índex de forma intel·ligent sense esborrar entrades existents no incloses.
     * Si es vol guardar una única enquesta, s'ha de passar un HashMap que la contingui.
     */
    public void guardarEnquestes(HashMap<String, Enquesta> enquestes) throws IOException {
        // 1. Guardar fitxers individuals
        for (Enquesta enquesta : enquestes.values()) {
            guardarFitxerEnquesta(enquesta);
        }
        
        // 2. Actualitzar índex (Smart Merge) amb tota la col·lecció
        actualitzarIndex(enquestes);
    }

    /**
     * Mètode intern privat per guardar només el fitxer físic.
     * S'encarrega de crear les carpetes i el fitxer 'enquesta.json'.
     */
    private void guardarFitxerEnquesta(Enquesta enquesta) throws IOException {
        // 1. Crear estructura de carpetas
        Path dirEnquesta = Paths.get(DIRECTORI_ENQUESTES, enquesta.getId());
        if (!Files.exists(dirEnquesta)) Files.createDirectories(dirEnquesta);
        
        Path dirPreguntes = dirEnquesta.resolve(DIR_PREGUNTES);
        Path dirRespostes = dirEnquesta.resolve(DIR_RESPOSTES);
        if (!Files.exists(dirPreguntes)) Files.createDirectories(dirPreguntes);
        if (!Files.exists(dirRespostes)) Files.createDirectories(dirRespostes);
        
        // 2. Guardar metadades (enquesta.json)
        JSONObject jsonEnquesta = new JSONObject();
        jsonEnquesta.put("id", enquesta.getId());
        jsonEnquesta.put("titol", enquesta.getTitol());
        jsonEnquesta.put("descripcio", enquesta.getDescripcio());
        jsonEnquesta.put("idCreador", enquesta.getIdCreador());
        
        JSONArray jsonParticipants = new JSONArray(enquesta.getParticipants());
        jsonEnquesta.put("participants", jsonParticipants);
        
        Path fitxerEnquesta = dirEnquesta.resolve(FITXER_ENQUESTA);
        Files.write(fitxerEnquesta, jsonEnquesta.toString(4).getBytes());
    }

    /**
     * LÒGICA OPTIMITZADA: Actualitza les entrades de l'índex usant un Mapa auxiliar.
     * Complexitat reduïda de O(N*M) a O(N+M).
     * @param enquestesActualitzades HashMap d'enquestes a actualitzar.
     */
    private void actualitzarIndex(HashMap<String, Enquesta> enquestesActualitzades) throws IOException {
        Path indexPath = Paths.get(DIRECTORI_ENQUESTES, FITXER_INDEX);
        
        // Mapa temporal per fer el merge (ID -> JSONObject)
        HashMap<String, JSONObject> indexMap = new HashMap<>();

        // 1. Carregar estat actual a memòria (O(N))
        if (Files.exists(indexPath)) {
            String content = new String(Files.readAllBytes(indexPath));
            if (!content.isEmpty()) {
                JSONArray currentArray = new JSONArray(content);
                for (int i = 0; i < currentArray.length(); i++) {
                    JSONObject entry = currentArray.getJSONObject(i);
                    indexMap.put(entry.getString("id"), entry);
                }
            }
        }

        // 2. Actualitzar o afegir les noves enquestes al Mapa (O(M))
        // El put del HashMap substitueix automàticament si la clau ja existeix (molt més ràpid que buscar)
        for (Enquesta enquesta : enquestesActualitzades.values()) {
            JSONObject nouEntry = new JSONObject();
            nouEntry.put("id", enquesta.getId());
            nouEntry.put("titol", enquesta.getTitol());
            nouEntry.put("creador", enquesta.getIdCreador());
            nouEntry.put("numPreguntes", enquesta.getPreguntes().size());
            nouEntry.put("numParticipants", enquesta.getNumParticipants());

            indexMap.put(enquesta.getId(), nouEntry);
        }

        // 3. Reconstruir el JSONArray i escriure (O(N+M))
        JSONArray finalArray = new JSONArray(indexMap.values());
        Files.write(indexPath, finalArray.toString(4).getBytes());
    }

    // -------------------------------------------------------------------------
    // ELIMINACIÓ
    // -------------------------------------------------------------------------

    /**
     * Elimina completament una enquesta del sistema.
     * Esborra tant el directori físic com l'entrada a l'índex global.
     */
    public void eliminarEnquestaCompleta(String idEnquesta) throws IOException {
        // 1. Eliminar directori físic i tot el seu contingut
        Path dirEnquesta = Paths.get(DIRECTORI_ENQUESTES, idEnquesta);
        if (Files.exists(dirEnquesta)) {
            try (Stream<Path> walk = Files.walk(dirEnquesta)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }

        // 2. Eliminar de l'índex
        eliminarEnquestaDeIndex(idEnquesta);
    }

    /**
     * Elimina l'entrada de l'enquesta del fitxer index.json.
     */
    private void eliminarEnquestaDeIndex(String idEnquesta) throws IOException {
        Path indexPath = Paths.get(DIRECTORI_ENQUESTES, FITXER_INDEX);
        if (!Files.exists(indexPath)) return;

        String content = new String(Files.readAllBytes(indexPath));
        if (content.isEmpty()) return;

        JSONArray jsonArray = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        boolean found = false;
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            if (!jsonEntry.getString("id").equals(idEnquesta)) {
                newArray.put(jsonEntry);
            } else {
                found = true;
            }
        }

        if (found) {
            Files.write(indexPath, newArray.toString(4).getBytes());
        }
    }

    // -------------------------------------------------------------------------
    // CÀRREGA
    // -------------------------------------------------------------------------

    public HashMap<String, Enquesta> carregarEnquestes(HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, Enquesta> enquestes = new HashMap<>();
        File dir = new File(DIRECTORI_ENQUESTES);

        if (!dir.exists()) return enquestes;

        File[] subdirs = dir.listFiles(File::isDirectory);
        if (subdirs == null) return enquestes;

        for (File subdir : subdirs) {
            try {
                String idEnquesta = subdir.getName();
                Enquesta enquesta = carregarDadesBasiquesEnquesta(idEnquesta, usuaris);
                if (enquesta != null) {
                    enquestes.put(enquesta.getId(), enquesta);
                }
            } catch (Exception e) {
                System.err.println("Error carregant enquesta " + subdir.getName() + ": " + e.getMessage());
            }
        }
        return enquestes;
    }

    public Enquesta carregarDadesBasiquesEnquesta(String idEnquesta, HashMap<String, Usuari> usuaris) throws IOException {
        Path path = Paths.get(DIRECTORI_ENQUESTES, idEnquesta, FITXER_ENQUESTA);
        if (!Files.exists(path)) return null;
        
        String content = new String(Files.readAllBytes(path));
        JSONObject jsonEnquesta = new JSONObject(content);
        
        String id = jsonEnquesta.getString("id");
        String titol = jsonEnquesta.getString("titol");
        String descripcio = jsonEnquesta.getString("descripcio");
        String idCreador = jsonEnquesta.getString("idCreador");

        Usuari creador = null;
        if (usuaris != null) creador = usuaris.get(idCreador);
        if (creador == null) creador = new Usuari(idCreador, "unknown");

        Enquesta enquesta = new Enquesta(id, titol, descripcio, creador);

        if (jsonEnquesta.has("participants")) {
            JSONArray jsonParticipants = jsonEnquesta.getJSONArray("participants");
            for (int k = 0; k < jsonParticipants.length(); k++) {
                enquesta.registrarParticipacio(jsonParticipants.getString(k));
            }
        }

        if (creador != null) creador.addEnquestaCreada(enquesta);

        return enquesta;
    }
    
    /**
     * Obté l'índex ràpid d'enquestes (per llistar sense carregar tot).
     */
    public JSONArray obtenirIndexEnquestes() throws IOException {
        Path indexPath = Paths.get(DIRECTORI_ENQUESTES, FITXER_INDEX);
        if (!Files.exists(indexPath)) return new JSONArray();
        
        String content = new String(Files.readAllBytes(indexPath));
        return content.isEmpty() ? new JSONArray() : new JSONArray(content);
    }
}