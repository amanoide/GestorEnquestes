package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.Usuari;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Gestor encarregat de la persistència dels usuaris en el sistema de fitxers.
 * <p>
 * Aquesta classe gestiona l'emmagatzematge i recuperació dels usuaris utilitzant fitxers JSON.
 * Manté un índex global per a un accés ràpid i fitxers individuals per a les dades detallades
 * de cada usuari.
 * </p>
 * <p>
 * <strong>Estructura de fitxers:</strong>
 * <ul>
 *   <li><code>dades/usuaris/index.json</code>: Índex global amb els noms d'usuari existents.</li>
 *   <li><code>dades/usuaris/{username}.json</code>: Fitxer individual amb les dades de l'usuari (password, enquestes participades).</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Característiques tècniques:</strong>
 * <ul>
 *   <li>Utilitza <code>java.nio</code> per a operacions de fitxers eficients.</li>
 *   <li>Garanteix la codificació <strong>UTF-8</strong>.</li>
 *   <li>Implementa estratègies d'actualització incremental (Smart Merge) per a l'índex.</li>
 * </ul>
 * </p>
 *
 * @author ClusterXX
 * @version 2.0
 */
public class GestorUsuaris {

    /** Ruta relativa al directori base on s'emmagatzemen els usuaris. */
    private static final String DIRECTORI_USUARIS = "dades/usuaris";

    /** Nom del fitxer d'índex global. */
    private static final String FITXER_INDEX = "index.json";

    /**
     * Mapa temporal per emmagatzemar els IDs de perfils al carregar usuaris.
     * Format: usuariUsername -> (idEnquesta -> perfilId)
     */
    private HashMap<String, HashMap<String, Long>> perfilsTemporals = new HashMap<>();

    /**
     * Constructor per defecte.
     * Els directoris es crearan automàticament quan sigui necessari guardar dades.
     */
    public GestorUsuaris() {
        // No crear directoris fins que sigui necessari
    }

    /**
     * Guarda un conjunt d'usuaris al sistema de persistència.
     * <p>
     * IMPORTANT: Aquest mètode SOBRESCRIU completament l'índex amb els usuaris proporcionats.
     * Això és útil per operacions batch o quan es vol persistir l'estat complet del sistema.
     * Per afegir usuaris incrementalment, utilitzar guardarUsuari().
     * </p>
     *
     * @param usuaris Mapa amb els usuaris a guardar, indexats per nom d'usuari.
     * @throws IOException Si es produeix un error d'entrada/sortida.
     */
    public void guardarUsuaris(HashMap<String, Usuari> usuaris) throws IOException {
        for (Usuari usuari : usuaris.values()) {
            guardarFitxerUsuari(usuari);
        }
        sobrescriureIndex(usuaris);
    }

    /**
     * Guarda o actualitza un únic usuari.
     * <p>
     * Aquesta operació és eficient ja que només escriu el fitxer de l'usuari específic
     * i actualitza la seva entrada a l'índex sense afectar altres usuaris (fa MERGE).
     * </p>
     *
     * @param usuari L'objecte Usuari a guardar.
     * @throws IOException Si es produeix un error d'entrada/sortida.
     */
    public void guardarUsuari(Usuari usuari) throws IOException {
        guardarFitxerUsuari(usuari);

        HashMap<String, Usuari> singleMap = new HashMap<>();
        singleMap.put(usuari.getUsername(), usuari);
        actualitzarIndexMerge(singleMap);
    }

    /**
     * Escriu les dades d'un usuari en el seu fitxer JSON corresponent.
     *
     * @param usuari L'usuari del qual es volen guardar les dades.
     * @throws IOException Si no es pot escriure el fitxer.
     */
    private void guardarFitxerUsuari(Usuari usuari) throws IOException {
        // Assegurar que el directori existeix
        File dir = new File(DIRECTORI_USUARIS);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        JSONObject jsonUsuari = new JSONObject();
        jsonUsuari.put("username", usuari.getUsername());
        jsonUsuari.put("password", usuari.getPassword());

        JSONArray enquestesParticipades = new JSONArray();
        JSONObject perfils = new JSONObject();
        if (usuari.getPerfils() != null) {
            for (java.util.Map.Entry<String, edu.upc.prop.clusterxx.domini.classes.Perfil> entry : 
                    usuari.getPerfils().entrySet()) {
                String idEnquesta = entry.getKey();
                edu.upc.prop.clusterxx.domini.classes.Perfil perfil = entry.getValue();
                
                enquestesParticipades.put(idEnquesta);
                
                // Guardar l'ID del perfil si existeix
                if (perfil != null) {
                    perfils.put(idEnquesta, perfil.getId());
                }
            }
        }
        jsonUsuari.put("enquestesParticipades", enquestesParticipades);
        jsonUsuari.put("perfils", perfils);

        Path path = Paths.get(DIRECTORI_USUARIS, usuari.getUsername() + ".json");
        Files.write(path, jsonUsuari.toString(4).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sobrescriu completament l'índex amb els usuaris proporcionats (mode REPLACE).
     * <p>
     * Aquest mètode elimina totes les entrades de l'índex que no estiguin en el mapa proporcionat.
     * </p>
     *
     * @param usuarisActualitzats Mapa dels usuaris que seran l'únic contingut de l'índex.
     * @throws IOException Si hi ha errors de lectura o escriptura.
     */
    private void sobrescriureIndex(HashMap<String, Usuari> usuarisActualitzats) throws IOException {
        // Assegurar que el directori existeix
        File dir = new File(DIRECTORI_USUARIS);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Path indexPath = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        JSONArray finalArray = new JSONArray();
        
        for (Usuari usuari : usuarisActualitzats.values()) {
            JSONObject entry = new JSONObject();
            entry.put("username", usuari.getUsername());
            finalArray.put(entry);
        }
        
        Files.write(indexPath, finalArray.toString(4).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Actualitza el fitxer d'índex global amb els usuaris proporcionats (mode MERGE).
     * <p>
     * Aquest mètode fa un MERGE intel·ligent: llegeix l'índex actual, actualitza o afegeix
     * les entrades dels usuaris proporcionats, i manté les entrades existents que no estan
     * en el mapa d'actualització.
     * </p>
     *
     * @param usuarisActualitzats Mapa dels usuaris que s'han d'actualitzar a l'índex.
     * @throws IOException Si hi ha errors de lectura o escriptura.
     */
    private void actualitzarIndexMerge(HashMap<String, Usuari> usuarisActualitzats) throws IOException {
        // Assegurar que el directori existeix
        File dir = new File(DIRECTORI_USUARIS);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Path indexPath = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        HashMap<String, JSONObject> indexMap = new HashMap<>();

        // Llegir índex existent si existeix
        if (Files.exists(indexPath)) {
            String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
            if (!content.isEmpty()) {
                JSONArray currentArray = new JSONArray(content);
                for (int i = 0; i < currentArray.length(); i++) {
                    JSONObject entry = currentArray.getJSONObject(i);
                    indexMap.put(entry.getString("username"), entry);
                }
            }
        }

        // Afegir o actualitzar usuaris
        for (Usuari usuari : usuarisActualitzats.values()) {
            JSONObject entry = new JSONObject();
            entry.put("username", usuari.getUsername());
            indexMap.put(usuari.getUsername(), entry);
        }

        // Escriure índex complet
        JSONArray finalArray = new JSONArray(indexMap.values());
        Files.write(indexPath, finalArray.toString(4).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Elimina completament un usuari del sistema.
     * <p>
     * Esborra el fitxer JSON de l'usuari i la seva entrada a l'índex global.
     * </p>
     *
     * @param username El nom d'usuari a eliminar.
     * @throws IOException Si hi ha errors durant l'eliminació.
     */
    public void eliminarUsuariComplet(String username) throws IOException {
        eliminarFitxerUsuari(username);
        eliminarUsuariDeIndex(username);
    }

    /**
     * Elimina el fitxer físic d'un usuari.
     *
     * @param username El nom de l'usuari.
     * @return true si el fitxer s'ha eliminat correctament, false altrament.
     */
    public boolean eliminarFitxerUsuari(String username) {
        File fitxer = new File(DIRECTORI_USUARIS, username + ".json");
        return fitxer.delete();
    }

    /**
     * Elimina l'entrada d'un usuari del fitxer d'índex.
     *
     * @param username El nom de l'usuari a eliminar de l'índex.
     * @throws IOException Si hi ha errors de lectura o escriptura.
     */
    private void eliminarUsuariDeIndex(String username) throws IOException {
        Path indexPath = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        File indexFile = indexPath.toFile();

        if (!indexFile.exists()) return;

        String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
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
            Files.write(indexPath, newArray.toString(4).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Carrega tots els usuaris registrats al sistema.
     * <p>
     * Llegeix l'índex global i carrega individualment cada usuari llistat.
     * </p>
     *
     * @return Un HashMap amb tots els usuaris carregats, indexats per nom d'usuari.
     * @throws IOException Si hi ha errors de lectura.
     */
    public HashMap<String, Usuari> carregarUsuaris() throws IOException {
        HashMap<String, Usuari> usuaris = new HashMap<>();
        Path indexPath = Paths.get(DIRECTORI_USUARIS, FITXER_INDEX);
        File indexFile = indexPath.toFile();

        if (!indexFile.exists()) return usuaris;

        String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
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

    /**
     * Carrega les dades d'un usuari específic des del seu fitxer.
     *
     * @param username El nom de l'usuari a carregar.
     * @return L'objecte Usuari carregat, o null si no existeix.
     * @throws IOException Si hi ha errors de lectura.
     */
    public Usuari carregarUsuari(String username) throws IOException {
        Path path = Paths.get(DIRECTORI_USUARIS, username + ".json");
        File fitxer = path.toFile();

        if (!fitxer.exists()) return null;

        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        JSONObject jsonUsuari = new JSONObject(content);

        String password = jsonUsuari.optString("password", "default");
        Usuari usuari = new Usuari(username, password);

        // Carregar enquestes participades i IDs de perfils si existeixen
        if (jsonUsuari.has("enquestesParticipades")) {
            JSONArray enquestesArray = jsonUsuari.getJSONArray("enquestesParticipades");
            JSONObject perfilsObject = jsonUsuari.optJSONObject("perfils");
            
            for (int i = 0; i < enquestesArray.length(); i++) {
                String idEnquesta = enquestesArray.getString(i);
                usuari.getPerfils().put(idEnquesta, null); // Es vinculará després
                
                // Si té un ID de perfil assignat, guardar-lo temporalment
                if (perfilsObject != null && perfilsObject.has(idEnquesta)) {
                    long perfilId = perfilsObject.getLong(idEnquesta);
                    if (!perfilsTemporals.containsKey(username)) {
                        perfilsTemporals.put(username, new HashMap<>());
                    }
                    perfilsTemporals.get(username).put(idEnquesta, perfilId);
                }
            }
        }

        return usuari;
    }

    /**
     * Obté el mapa temporal d'IDs de perfils carregats durant la càrrega d'usuaris.
     * Aquest mapa s'utilitza per vincular correctament els perfils amb els usuaris després de carregar-los.
     *
     * @return Mapa amb username -> (idEnquesta -> perfilId)
     */
    public HashMap<String, HashMap<String, Long>> getPerfilsTemporals() {
        return perfilsTemporals;
    }

    /**
     * Neteja el mapa temporal de perfils.
     * S'ha de cridar després de vincular correctament els perfils amb els usuaris.
     */
    public void netejarPerfilsTemporals() {
        perfilsTemporals.clear();
    }
}