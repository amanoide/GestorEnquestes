package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Gestor encarregat de la persistència de les respostes en el sistema de fitxers.
 * <p>
 * Aquesta classe gestiona l'emmagatzematge i recuperació de les respostes dels usuaris a les enquestes.
 * Utilitza una estructura jeràrquica on les respostes s'agrupen per enquesta i després per usuari.
 * </p>
 * <p>
 * <strong>Estructura de fitxers:</strong>
 * <ul>
 *   <li><code>dades/enquestes/{id_enquesta}/respostes/index.json</code>: Índex amb la llista d'usuaris que han respost.</li>
 *   <li><code>dades/enquestes/{id_enquesta}/respostes/{username}.json</code>: Fitxer amb totes les respostes d'un usuari específic per a aquesta enquesta.</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Característiques tècniques:</strong>
 * <ul>
 *   <li>Utilitza <code>java.nio</code> per a operacions de fitxers eficients.</li>
 *   <li>Garanteix la codificació <strong>UTF-8</strong>.</li>
 *   <li>Implementa estratègies d'actualització incremental (Smart Merge) per a l'índex de participants.</li>
 * </ul>
 * </p>
 *
 * @author ClusterXX
 * @version 2.0
 */
public class GestorRespostes {

    /** Ruta base del directori d'enquestes. */
    private static final String DIRECTORI_BASE = "dades/enquestes";

    /** Nom del subdirectori on s'emmagatzemen les respostes. */
    private static final String SUBDIR_RESPOSTES = "respostes";

    /** Nom del fitxer d'índex de participants. */
    private static final String FITXER_INDEX = "index.json";

    /**
     * Constructor per defecte.
     */
    public GestorRespostes() {
    }

    /**
     * Guarda totes les respostes d'una enquesta al sistema de persistència.
     * <p>
     * Aquest mètode agrupa les respostes per usuari, guarda els fitxers individuals de cada usuari
     * i actualitza l'índex de participants de l'enquesta.
     * </p>
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @param preguntes Llista de preguntes que contenen les respostes a guardar.
     * @throws IOException Si es produeix un error d'entrada/sortida.
     */
    public void guardarRespostes(String idEnquesta, ArrayList<Pregunta> preguntes) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        if (!dirRespostes.exists()) {
            dirRespostes.mkdirs();
        }

        HashMap<String, HashMap<String, Resposta>> respostesPorUsuari = new HashMap<>();
        
        for (Pregunta pregunta : preguntes) {
            for (Resposta resposta : pregunta.getRespostes().values()) {
                String username = resposta.getUsernameUsuari();
                respostesPorUsuari.putIfAbsent(username, new HashMap<>());
                respostesPorUsuari.get(username).put(resposta.getId(), resposta);
            }
        }

        // Eliminar fitxers d'usuaris que ja no tenen respostes
        File[] existingFiles = dirRespostes.listFiles((d, name) -> name.endsWith(".json") && !name.equals(FITXER_INDEX));
        if (existingFiles != null) {
            for (File f : existingFiles) {
                String filename = f.getName();
                String username = filename.substring(0, filename.lastIndexOf('.'));
                if (!respostesPorUsuari.containsKey(username)) {
                    f.delete();
                }
            }
        }

        guardarIndex(idEnquesta, respostesPorUsuari.keySet());
        
        for (Map.Entry<String, HashMap<String, Resposta>> entry : respostesPorUsuari.entrySet()) {
            guardarRespostesUsuari(idEnquesta, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Guarda les respostes d'un usuari específic per a una enquesta concreta.
     * <p>
     * Crea o sobreescriu el fitxer JSON de l'usuari dins del directori de respostes de l'enquesta.
     * </p>
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @param username El nom de l'usuari que ha respost.
     * @param respostes Mapa de les respostes de l'usuari.
     * @throws IOException Si no es pot escriure el fitxer.
     */
    public void guardarRespostesUsuari(String idEnquesta, String username, HashMap<String, Resposta> respostes) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        if (!dirRespostes.exists()) {
            dirRespostes.mkdirs();
        }

        // Si no hi ha respostes, eliminar el fitxer si existeix
        if (respostes.isEmpty()) {
            eliminarRespostesUsuari(idEnquesta, username);
            return;
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
        
        Path fitxer = dirRespostes.toPath().resolve(username + ".json");
        Files.write(fitxer, jsonRespostes.toString(4).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Actualitza l'índex de participants d'una enquesta.
     * <p>
     * Sobreescriu l'índex amb la llista actual d'usuaris que tenen respostes.
     * </p>
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @param nousUsuaris Conjunt de noms d'usuari a afegir a l'índex.
     * @throws IOException Si hi ha errors de lectura o escriptura.
     */
    private void guardarIndex(String idEnquesta, Set<String> nousUsuaris) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, FITXER_INDEX);
        
        JSONArray jsonArray = new JSONArray();
        for (String username : nousUsuaris) {
            jsonArray.put(username);
        }
        
        Files.write(fitxer.toPath(), jsonArray.toString(4).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Elimina el fitxer de respostes d'un usuari per a una enquesta.
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @param username El nom de l'usuari.
     * @return true si el fitxer s'ha eliminat correctament, false altrament.
     */
    public boolean eliminarRespostesUsuari(String idEnquesta, String username) {
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, username + ".json");
        return fitxer.delete();
    }

    /**
     * Carrega les respostes d'un usuari específic per a una enquesta.
     * <p>
     * Llegeix el fitxer JSON de l'usuari i reconstrueix els objectes Resposta.
     * </p>
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @param username El nom de l'usuari.
     * @param usuaris Mapa d'usuaris per resoldre la referència a l'objecte Usuari.
     * @return Un HashMap amb les respostes de l'usuari, on la clau és l'ID de la pregunta.
     * @throws IOException Si hi ha errors de lectura.
     */
    public HashMap<String, Resposta> carregarRespostesUsuari(String idEnquesta, String username, HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, Resposta> respostes = new HashMap<>();
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, username + ".json");

        if (!fitxer.exists()) {
            return respostes;
        }

        String content = new String(Files.readAllBytes(fitxer.toPath()), StandardCharsets.UTF_8);
        
        if (content.isEmpty()) {
            return respostes;
        }

        JSONArray jsonRespostes = new JSONArray(content);
        
        for (int i = 0; i < jsonRespostes.length(); i++) {
            JSONObject jsonResposta = jsonRespostes.getJSONObject(i);
            String idResposta = jsonResposta.getString("id");
            String idPregunta = jsonResposta.getString("idPregunta");
            String usernameResposta = jsonResposta.getString("username");
            String textResposta = jsonResposta.getString("text");

            Usuari usuari = null;
            if (usuaris != null) {
                usuari = usuaris.get(usernameResposta);
            }
            if (usuari == null) {
                usuari = new Usuari(usernameResposta, "");
            }

            Resposta resposta = new Resposta(idResposta, idPregunta, textResposta, usuari);
            respostes.put(idPregunta, resposta);
        }

        return respostes;
    }

    /**
     * Carrega totes les respostes de tots els usuaris per a una enquesta.
     * <p>
     * Explora el directori de respostes de l'enquesta i carrega cada fitxer d'usuari trobat.
     * </p>
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @param usuaris Mapa d'usuaris per vincular les respostes amb els seus autors.
     * @return Un HashMap on la clau és el nom d'usuari i el valor és un mapa de les seves respostes.
     * @throws IOException Si hi ha errors de lectura.
     */
    public HashMap<String, HashMap<String, Resposta>> carregarTotsRespostes(String idEnquesta, HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, HashMap<String, Resposta>> totesRespostes = new HashMap<>();
        File dirRespostes = getDirRespostes(idEnquesta);

        if (!dirRespostes.exists()) {
            return totesRespostes;
        }

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
     * Carrega totes les respostes de totes les enquestes passades i retorna un mapa global
     * on la clau és l'ID de la resposta i el valor és l'objecte Resposta.
     * <p>
     * Aquest mètode actua com a adaptador per a crides que volen un mapa pla de respostes
     * a partir d'un mapa d'enquestes.
     * </p>
     * 
     * @param enquestes Mapa d'enquestes de les quals carregar respostes
     * @return HashMap amb totes les respostes indexades per ID de resposta
     * @throws IOException Si hi ha error de lectura
     */
    public HashMap<String, Resposta> carregarRespostes(HashMap<String, Enquesta> enquestes) throws IOException {
        HashMap<String, Resposta> all = new HashMap<>();
        if (enquestes == null) return all;

        for (String idEnquesta : enquestes.keySet()) {
            try {
                HashMap<String, HashMap<String, Resposta>> perUsuari = carregarTotsRespostes(idEnquesta, null);
                for (HashMap<String, Resposta> userMap : perUsuari.values()) {
                    for (Resposta r : userMap.values()) {
                        if (r != null) all.put(r.getId(), r);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error carregant respostes per enquesta " + idEnquesta + ": " + e.getMessage());
            }
        }

        return all;
    }

    /**
     * Obté l'objecte File que representa el directori de respostes d'una enquesta.
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @return L'objecte File del directori.
     */
    private File getDirRespostes(String idEnquesta) {
        return new File(DIRECTORI_BASE + File.separator + idEnquesta + File.separator + SUBDIR_RESPOSTES);
    }

    /**
     * Obté la llista d'usuaris que han participat en una enquesta.
     * <p>
     * Llegeix el fitxer d'índex de participants sense necessitat de carregar totes les respostes.
     * </p>
     *
     * @param idEnquesta L'identificador de l'enquesta.
     * @return Un JSONArray amb els noms d'usuari dels participants.
     * @throws IOException Si hi ha errors de lectura.
     */
    public JSONArray obtenirIndexRespostes(String idEnquesta) throws IOException {
        File dirRespostes = getDirRespostes(idEnquesta);
        File fitxer = new File(dirRespostes, FITXER_INDEX);
        
        if (!fitxer.exists()) {
            return new JSONArray();
        }

        String content = new String(Files.readAllBytes(fitxer.toPath()), StandardCharsets.UTF_8);
        if (content.isEmpty()) {
            return new JSONArray();
        }

        return new JSONArray(content);
    }
}